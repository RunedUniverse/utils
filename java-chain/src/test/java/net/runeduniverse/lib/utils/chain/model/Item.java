package net.runeduniverse.lib.utils.chain.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString(callSuper = true)
public class Item extends AEntity {
	private Inventory containingInventory;

	private String str = "my string";
	private Boolean bool = false;

	private String itemStackData = null;

	@Getter
	private String itemStack = null;
}
