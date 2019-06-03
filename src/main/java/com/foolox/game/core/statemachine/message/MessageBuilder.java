package com.foolox.game.core.statemachine.message;

import com.foolox.game.core.engin.game.state.PlayerEvent;

/**
 * The default message builder; creates immutable {@link GenericMessage}s.
 * Named MessageBuilder instead of DefaultMessageBuilder for backwards
 * compatibility.
 *
 * @author Arjen Poutsma
 * @author Mark Fisher
 * @author Oleg Zhurakousky
 * @author Dave Syer
 * @author Gary Russell
 * @author Artem Bilan
 */
public final class MessageBuilder  {

	private final PlayerEvent payload;

	private MessageHeaders readOnlyHeaders = new MessageHeaders();


	/**
	 * Private constructor to be invoked from the static factory methods only.
	 */
	private MessageBuilder(PlayerEvent payload) {
		this.payload = payload;
	}

	public PlayerEvent getPayload() {
		return this.payload;
	}

	public MessageBuilder setHeader(String headerName, Object headerValue) {
		this.readOnlyHeaders.getHeaders().put(headerName, headerValue) ;
		return this;
	}
	
	/**
	 * Create a builder for a new {@link Message} instance with the provided payload.
	 *
	 * @param payload the payload for the new message
	 * @return A MessageBuilder.
	 */
	public static MessageBuilder withPayload(PlayerEvent payload) {
		return new MessageBuilder(payload);
	}

	public Message build() {
		return new GenericMessage(this.payload, this.readOnlyHeaders);
	}


}