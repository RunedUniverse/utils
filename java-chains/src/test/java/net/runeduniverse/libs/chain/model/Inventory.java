package net.runeduniverse.libs.chain.model;

import java.util.HashSet;
import java.util.Set;

import lombok.Getter;
import lombok.Setter;
import net.runeduniverse.libs.chain.model.relations.Slot;

@Getter
@Setter
public class Inventory extends AEntity {
	private Set<Slot> slots = new HashSet<>();

	private Integer size = -1;
	private String inventory = null;
}
