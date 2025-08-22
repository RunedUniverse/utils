/*
 * Copyright © 2025 VenaNocta (venanocta@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.runeduniverse.lib.utils.maven3.ext;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.maven.execution.MavenSession;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.classworlds.realm.ClassRealm;
import org.codehaus.plexus.component.annotations.Component;
import org.codehaus.plexus.component.annotations.Requirement;
import org.codehaus.plexus.logging.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import net.runeduniverse.lib.utils.maven3.ext.api.ExtensionTool;

@Component(role = ExtensionTool.class)
public class DefaultExtensionTool implements ExtensionTool {

	public static final String RESOURCE_PATH_XML_FORMAT_FIX_XSLT = "META-INF/xml-format/xml-format-fix.xslt";
	public static final String RESOURCE_PATH_XML_EXTENSION = "META-INF/xml-template/extensions.xml";
	public static final String REALM_ID_BUILD_EXT_TEMPLATE = "extension>%s:%s";

	@Requirement
	private Logger log;

	@Override
	public boolean setupAsCoreExtension(final MavenSession mvnSession, final MavenProject mvnProject,
			final String groupId, final String artifactId, final String version) throws IOException {
		final Path mvnExtConfig = Paths.get(mvnSession.getExecutionRootDirectory(), ".mvn", "extensions.xml");
		// ensure .mvn folder exists
		final Path mvnCnfFolder = mvnExtConfig.getParent();
		if (Files.notExists(mvnCnfFolder)) {
			try {
				this.log.debug("Creating folder » " + mvnCnfFolder);
				Files.createDirectories(mvnCnfFolder);
			} catch (IOException e) {
				new IOException("Failed to create maven config folder!", e);
			}
		} else if (!Files.isDirectory(mvnCnfFolder)) {
			throw new IOException("Maven config folder is a file!");
		}
		// ensure extensions.xml file is modifyable
		if (Files.exists(mvnExtConfig)) {
			if (!Files.isRegularFile(mvnExtConfig))
				throw new IOException("Maven extension config file is not a file!");
			if (!Files.isReadable(mvnExtConfig))
				throw new IOException("Maven extension config file is not readable!");
			if (!Files.isWritable(mvnExtConfig))
				throw new IOException("Maven extension config file is not writeable!");
		}

		if (!setupAsCoreExtension(mvnSession, mvnExtConfig, groupId, artifactId, version))
			return false;

		boolean buildExtActive = false;
		final ClassRealm prjRealm = mvnProject.getClassRealm();
		if (prjRealm != null) {
			for (ClassRealm realm : prjRealm.getWorld()
					.getRealms()) {
				if (realm == null)
					continue;
				if (realm.getId()
						.startsWith(String.format(REALM_ID_BUILD_EXT_TEMPLATE, groupId, artifactId)))
					buildExtActive = true;
			}
		}

		final MavenProject topLevelMvnProject = mvnSession.getTopLevelProject();

		for (MavenProject project : mvnSession.getAllProjects()) {
			final File pomFile = project.getFile();
			if (pomFile == null || !pomFile.exists())
				continue;
			boolean create = false;
			// ensure we disable the build-extension if it is loaded by inheritance
			if (buildExtActive && topLevelMvnProject == project)
				create = true;
			try {
				setupAsBuildExtension(pomFile.toPath(), groupId, artifactId, version, create, false);
			} catch (Exception ignored) {
			}
		}
		return true;
	}

	private boolean setupAsCoreExtension(final MavenSession mvnSession, final Path mvnExtConfig, final String groupId,
			final String artifactId, final String version) throws IOException {
		// modify extensions.xml
		final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		final DocumentBuilder builder;
		try {
			builder = factory.newDocumentBuilder();
		} catch (ParserConfigurationException ignored) {
			this.log.error("unexpected exception in " + getClass().getCanonicalName() + ".setupAsCoreExtension()",
					ignored);
			return false;
		}

		final AtomicBoolean dirty = new AtomicBoolean(false);
		final Document document;
		if (Files.exists(mvnExtConfig)) {
			try (InputStream inSream = new FileInputStream(mvnExtConfig.toFile())) {
				document = builder.parse(inSream);
			} catch (IOException e) {
				throw e;
			} catch (SAXException e) {
				throw new IOException("Maven extension-configuration ( .mvn/extensions.xml ) is broken!"
						+ " Check the official reference ( https://maven.apache.org/configure.html ) for the correct configuration!");
			}
		} else {
			try (InputStream inSream = getClass().getClassLoader()
					.getResourceAsStream(RESOURCE_PATH_XML_EXTENSION)) {
				document = builder.parse(inSream);
				dirty.set(true);
			} catch (IOException e) {
				throw e;
			} catch (SAXException ignored) {
				this.log.error("unexpected exception in " + getClass().getCanonicalName() + ".setupAsCoreExtension()",
						ignored);
				return false;
			}
		}

		final Element ext = acquireExtensionElementFromExtXml(document, groupId, artifactId);
		final Element versionElement = acquireElement(document, ext, dirty, "version", true);
		// return if version isn't changing
		String oldVersion = versionElement.getTextContent();
		if (oldVersion == null || !version.equals(oldVersion.trim())) {
			versionElement.setTextContent(version);
			dirty.compareAndSet(false, true);
		}

		if (!dirty.get()) {
			// no elements have been changed! skip writing!
			return true;
		}

		final TransformerFactory transformerFactory = TransformerFactory.newInstance();
		final Transformer transformer;
		try {
			transformer = transformerFactory.newTransformer(new StreamSource(getClass().getClassLoader()
					.getResourceAsStream(RESOURCE_PATH_XML_FORMAT_FIX_XSLT)));
		} catch (TransformerConfigurationException ignored) {
			this.log.error("unexpected exception in " + getClass().getCanonicalName() + ".setupAsCoreExtension()",
					ignored);
			return false;
		}

		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		transformer.setOutputProperty(OutputKeys.STANDALONE, "no");

		final DOMSource source = new DOMSource(document);

		try (FileOutputStream outStream = new FileOutputStream(mvnExtConfig.toFile())) {
			final StreamResult result = new StreamResult(outStream);
			transformer.transform(source, result);
		} catch (TransformerException ignored) {
			this.log.error("unexpected exception in " + getClass().getCanonicalName() + ".setupAsCoreExtension()",
					ignored);
			return false;
		}

		return true;
	}

	private Element acquireExtensionElementFromExtXml(final Document doc, final String groupId,
			final String artifactId) {
		final Element docElement = doc.getDocumentElement();
		final NodeList extList = docElement.getElementsByTagName("extension");
		// find the extension in case it is already defined
		for (int i = 0; i < extList.getLength(); i++) {
			final Node node = extList.item(i);
			if (!(node instanceof Element))
				continue;

			final Element ext = (Element) node;
			final NodeList values = ext.getChildNodes();
			boolean foundGroupId = false;
			boolean foundArtifactId = false;

			for (int j = 0; j < values.getLength(); j++) {
				final Node val = values.item(j);
				if (!(val instanceof Element))
					continue;

				final Element e = (Element) val;
				String content = e.getTextContent();
				if (content != null)
					content = content.trim();
				switch (e.getTagName()) {
				case "groupId":
					if (groupId.equals(content))
						foundGroupId = true;
					break;
				case "artifactId":
					if (artifactId.equals(content))
						foundArtifactId = true;
					break;
				}
			}
			if (foundGroupId && foundArtifactId)
				return ext;
		}
		// in case it doesnt already exist => create a new one
		final Element ext = doc.createElement("extension");
		docElement.appendChild(ext);
		final Element eGroupId = doc.createElement("groupId");
		eGroupId.setTextContent(groupId);
		ext.appendChild(eGroupId);
		final Element eArtifactId = doc.createElement("artifactId");
		eArtifactId.setTextContent(artifactId);
		ext.appendChild(eArtifactId);
		return ext;
	}

	@Override
	public boolean setupAsBuildExtension(final MavenSession mvnSession, final MavenProject mvnProject,
			final String groupId, final String artifactId, final String version) throws IOException {
		final File pomFile = mvnProject.getFile();
		if (pomFile == null || !pomFile.exists())
			return false;
		return setupAsBuildExtension(pomFile.toPath(), groupId, artifactId, version, true, true);
	}

	private boolean setupAsBuildExtension(final Path mvnPomPath, final String groupId, final String artifactId,
			final String version, final boolean create, final boolean enable) throws IOException {
		// modify pom.xml
		final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		final DocumentBuilder builder;
		try {
			builder = factory.newDocumentBuilder();
		} catch (ParserConfigurationException ignored) {
			this.log.error("unexpected exception in " + getClass().getCanonicalName() + ".setupAsBuildExtension()",
					ignored);
			return false;
		}
		final Document document;
		try (InputStream inSream = new FileInputStream(mvnPomPath.toFile())) {
			document = builder.parse(inSream);
		} catch (IOException e) {
			throw e;
		} catch (SAXException e) {
			throw new IOException("Maven pom-configuration ( pom.xml ) is broken!"
					+ " Check the official reference ( https://maven.apache.org/pom.html ) for the correct configuration!");
		}

		final AtomicBoolean dirty = new AtomicBoolean(false);
		final Element docElement = document.getDocumentElement();
		final Element pluginElement = acquirePluginElementFromPomXml(document, docElement, dirty, groupId, artifactId,
				version, create);
		// if it doesn't exist and create == false => return true else false
		if (pluginElement == null) {
			if (create)
				return false;
		} else
			setExtensionsFlag(document, pluginElement, dirty, create, enable);
		// when enable==false, to be sure all profiles have to be checked
		if (!enable) {
			final Element profilesElement = acquireElement(document, docElement, dirty, "profiles", false);
			if (profilesElement != null) {
				final NodeList profileList = profilesElement.getChildNodes();
				for (int i = 0; i < profileList.getLength(); i++) {
					final Node node = profileList.item(i);
					if (!(node instanceof Element) || !"profile".equals(node.getNodeName()))
						continue;
					final Element profilePluginElement = acquirePluginElementFromPomXml(document, (Element) node, dirty,
							groupId, artifactId, version, false);
					if (profilePluginElement == null)
						continue;
					setExtensionsFlag(document, profilePluginElement, dirty, false, false);
				}
			}
		}

		if (!dirty.get()) {
			// no elements have been changed! skip writing!
			return true;
		}

		final TransformerFactory transformerFactory = TransformerFactory.newInstance();
		final Transformer transformer;
		try {
			transformer = transformerFactory.newTransformer(new StreamSource(getClass().getClassLoader()
					.getResourceAsStream(RESOURCE_PATH_XML_FORMAT_FIX_XSLT)));
		} catch (TransformerConfigurationException ignored) {
			this.log.error("unexpected exception in " + getClass().getCanonicalName() + ".setupAsBuildExtension()",
					ignored);
			return false;
		}

		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		transformer.setOutputProperty(OutputKeys.STANDALONE, "no");

		final DOMSource source = new DOMSource(document);

		try (FileOutputStream outStream = new FileOutputStream(mvnPomPath.toFile())) {
			final StreamResult result = new StreamResult(outStream);
			transformer.transform(source, result);
		} catch (TransformerException ignored) {
			this.log.error("unexpected exception in " + getClass().getCanonicalName() + ".setupAsBuildExtension()",
					ignored);
			return false;
		}
		return true;
	}

	private Element acquirePluginElementFromPomXml(final Document doc, final Element parentElement,
			final AtomicBoolean dirty, final String groupId, final String artifactId, final String version,
			final boolean create) {
		final Element buildElement = acquireElement(doc, parentElement, dirty, "build", create);
		if (buildElement == null)
			return null;
		final Element pluginsElement = acquireElement(doc, buildElement, dirty, "plugins", create);
		if (pluginsElement == null)
			return null;

		final NodeList list = pluginsElement.getElementsByTagName("plugin");
		for (int i = 0; i < list.getLength(); i++) {
			final Node node = list.item(i);
			if (!(node instanceof Element))
				continue;
			final Element pluginElement = (Element) node;
			final NodeList elements = pluginElement.getChildNodes();
			boolean foundGroupId = false;
			boolean foundArtifactId = false;

			for (int j = 0; j < elements.getLength(); j++) {
				final Node val = elements.item(j);
				if (!(val instanceof Element))
					continue;

				final Element e = (Element) val;
				String content = e.getTextContent();
				if (content != null)
					content = content.trim();
				switch (e.getTagName()) {
				case "groupId":
					if (groupId.equals(content))
						foundGroupId = true;
					break;
				case "artifactId":
					if (artifactId.equals(content))
						foundArtifactId = true;
					break;
				}
			}
			if (foundGroupId && foundArtifactId) {
				return pluginElement;
			}
		}
		if (!create)
			return null;
		// in case it doesnt already exist => create a new one
		final Element plugin = doc.createElement("plugin");
		pluginsElement.appendChild(plugin);
		final Element eGroupId = doc.createElement("groupId");
		eGroupId.setTextContent(groupId);
		plugin.appendChild(eGroupId);
		final Element eArtifactId = doc.createElement("artifactId");
		eArtifactId.setTextContent(artifactId);
		plugin.appendChild(eArtifactId);
		final Element eVersion = doc.createElement("version");
		eVersion.setTextContent(version);
		plugin.appendChild(eVersion);
		dirty.compareAndSet(false, true);
		return plugin;
	}

	private boolean setExtensionsFlag(final Document document, final Element pluginElement, final AtomicBoolean dirty,
			final boolean create, final boolean enable) {
		final Element flagElement = acquireElement(document, pluginElement, dirty, "extensions", create);
		if (flagElement == null)
			return !enable;
		final String newState = enable ? "true" : "false";
		final String oldState = flagElement.getTextContent();
		// skip writing if nothing changes!
		if (newState.equals(oldState))
			return true;
		flagElement.setTextContent(newState);
		dirty.compareAndSet(false, true);
		return true;
	}

	private Element acquireElement(final Document doc, final Element parentElement, final AtomicBoolean dirty,
			final String tagName, final boolean create) {
		final NodeList list = parentElement.getChildNodes();
		for (int i = 0; i < list.getLength(); i++) {
			final Node node = list.item(i);
			if (!(node instanceof Element) || !tagName.equals(node.getNodeName()))
				continue;
			return (Element) node;
		}
		if (!create)
			return null;
		final Element element = doc.createElement(tagName);
		parentElement.appendChild(element);
		dirty.compareAndSet(false, true);
		return element;
	}
}
