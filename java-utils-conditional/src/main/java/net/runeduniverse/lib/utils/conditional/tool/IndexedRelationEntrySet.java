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
package net.runeduniverse.lib.utils.conditional.tool;

public class IndexedRelationEntrySet<T> extends RelationEntrySet<T> {

	private static final long serialVersionUID = 1L;

	protected final ConditionIndexer indexer;

	public IndexedRelationEntrySet(final ConditionIndexer indexer) {
		this.indexer = indexer;
	}

	public IndexedRelationEntrySet(final ConditionIndexer indexer, int initialCapacity) {
		super(initialCapacity);
		this.indexer = indexer;
	}

	public IndexedRelationEntrySet(final ConditionIndexer indexer, int initialCapacity, float loadFactor) {
		super(initialCapacity, loadFactor);
		this.indexer = indexer;
	}

	@Override
	protected IndexedRelationEntrySet<T> newInstance() {
		return new IndexedRelationEntrySet<>(this.indexer);
	}

	@Override
	public IndexedRelationEntrySet<T> compile(final ConditionIndexer indexer) {
		final IndexedRelationEntrySet<T> set = newInstance();
		compileTo(indexer, set);
		return set;
	}

	public IndexedRelationEntrySet<T> compile() {
		final IndexedRelationEntrySet<T> set = newInstance();
		compileTo(this.indexer, set);
		return set;
	}
}
