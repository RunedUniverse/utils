package net.runeduniverse.libs.chain.model.relations;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.runeduniverse.libs.chain.model.Inventory;
import net.runeduniverse.libs.chain.model.Item;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Slot extends ARelationEntity {
	private Integer slot;
	private Inventory inventory;
	private Item item;
}
