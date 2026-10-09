package com.beepdeep;

import com.google.gson.annotations.SerializedName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.runelite.client.party.messages.PartyMemberMessage;

/**
 * Party payload: a member triggered a Beep Deep event and chose a sound slot,
 * so other members can play the same slot from their own config.
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class BeepDeepPartyMessage extends PartyMemberMessage
{
	/** {@link ToaEvent#name()} of the triggered event. */
	@SerializedName("e")
	private String event;
	/** Zero-based slot index chosen by the sender. */
	@SerializedName("s")
	private int slotIndex;
}