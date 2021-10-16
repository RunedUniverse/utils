package net.runeduniverse.libs.chain.model;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Player {

	@Setter
	private UUID uuid;

	@Setter
	private String name;

	@Setter
	private Inventory inventory;
}
