/*
 * (c) Copyright 2024 Swiss Post Ltd.
 */
package ch.post.it.evoting.dataintegrationservice.parameter.plugin;

import java.util.List;

public interface ExtendedAuthenticationKeyPlugin {
	List<String> getKeyNames();
}
