/*
 * (c) Copyright 2023 Swiss Post Ltd.
 */
package ch.post.it.evoting.dataintegrationservice.parameter.plugin;

import java.util.List;

public interface ExtendedAuthenticationKeyPlugin {
	List<String> getKeyNames();
}
