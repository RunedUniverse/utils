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

import net.runeduniverse.lib.utils.logging.log.api.CompoundTreeStyle;
import net.runeduniverse.lib.utils.logging.log.api.LogEntry;
import net.runeduniverse.lib.utils.logging.log.api.TreeRecord;

public class LineEntry implements LogEntry {

	protected final CompoundTreeStyle style;

	protected CharSequence tag;
	protected CharSequence content;

	public LineEntry(final CompoundTreeStyle style, final CharSequence content) {
		this(style, null, content);
	}

	public LineEntry(final CompoundTreeStyle style, final CharSequence tag, final CharSequence content) {
		this.style = style;
		this.tag = tag;
		this.content = content;
	}

	@Override
	public void toRecord(final TreeRecord record) {
		record.append(new DefaultLineRecord(this.style, this.tag, this.content));
	}
}
