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
package net.runeduniverse.lib.utils.logging.log;

import lombok.Getter;
import net.runeduniverse.lib.utils.logging.log.api.CompoundTreeStyle;
import net.runeduniverse.lib.utils.logging.log.api.LineRecord;

public class DefaultLineRecord implements LineRecord {

	protected final CompoundTreeStyle style;

	protected CharSequence tag = null;
	protected CharSequence content = null;
	@Getter
	private int tagSize;

	public DefaultLineRecord(final CompoundTreeStyle style, final CharSequence content) {
		this(style, null, content);
	}

	public DefaultLineRecord(final CompoundTreeStyle style, final CharSequence tag, final CharSequence content) {
		this.style = style;
		if (tag == null)
			this.tagSize = 0;
		else {
			this.tag = this.style.getTagsToUpper() ? tag.toString()
					.toUpperCase() : tag.toString();
			this.tagSize = this.tag.length();
		}
		this.content = content;
	}

	protected String buildLine(final int tagSize) {
		if (this.tag == null)
			return (String) this.content + '\n';

		final StringBuilder builder = new StringBuilder(String.format(this.style.getTaggedTxtBox()
				.toString(), this.tag));
		for (int i = 0; i < (tagSize - this.tagSize); i++)
			builder.append(' ');
		builder.append(this.style.getTaggedTxtSplitter())
				.append(this.content)
				.append('\n');
		return builder.toString();
	}

	public String write(final CharSequence[] baseOffset, final int tagSize) {
		return String.join("", baseOffset) + this.buildLine(tagSize);
	}
}
