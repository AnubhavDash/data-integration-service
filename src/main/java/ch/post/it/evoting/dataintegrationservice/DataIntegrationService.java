/*
 * (c) Copyright 2023 Swiss Post Ltd.
 */
package ch.post.it.evoting.dataintegrationservice;

import static com.google.common.base.Preconditions.checkState;

import java.io.File;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryPoolMXBean;
import java.lang.management.MemoryType;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import li.chee.reactive.plumber.Runtime;

@Service
public class DataIntegrationService {
	private static final Logger LOGGER = LoggerFactory.getLogger(DataIntegrationService.class);
	@Value("${version}")
	private String version;

	@Value("${generateGraphs:false}")
	private String generateGraphs;

	@Value("${directory.target:target}")
	private String targetDirectory;

	public void run() {
		try {
			LOGGER.info("Starting Data Integration Service (V{})", version);
			final long startTime = System.currentTimeMillis();

			ensureTargetDirectoryExists();

			final URI[] preVoteSteps = { DataIntegrationServiceApplication.class.getResource("/pipes/inputPhase.groovy").toURI(),

					DataIntegrationServiceApplication.class.getResource("/pipes/parameterPhase.groovy").toURI(),

					DataIntegrationServiceApplication.class.getResource("/pipes/cantonalTree/stdv1/cantonalTreeStdv1.groovy").toURI(),
					DataIntegrationServiceApplication.class.getResource("/pipes/cantonalTreePhase.groovy").toURI(),

					DataIntegrationServiceApplication.class.getResource("/pipes/contest/ech015xv3/contestEch015xv3.groovy").toURI(),
					DataIntegrationServiceApplication.class.getResource("/pipes/contest/ech015xv4/contestEch015xv4.groovy").toURI(),
					DataIntegrationServiceApplication.class.getResource("/pipes/contestPhase.groovy").toURI(),

					DataIntegrationServiceApplication.class.getResource("/pipes/register/ech0045v3/registerEch0045v3.groovy").toURI(),
					DataIntegrationServiceApplication.class.getResource("/pipes/register/ech0045v4/registerEch0045v4.groovy").toURI(),
					DataIntegrationServiceApplication.class.getResource("/pipes/registerPhase.groovy").toURI(),

					DataIntegrationServiceApplication.class.getResource("/pipes/authorizationPhase.groovy").toURI(),

					DataIntegrationServiceApplication.class.getResource("/pipes/assigningAuthorizationPhase.groovy").toURI(),

					DataIntegrationServiceApplication.class.getResource("/pipes/headerPhase.groovy").toURI(),

					DataIntegrationServiceApplication.class.getResource("/pipes/outputPrePhase.groovy").toURI() };

			new Runtime("true".equalsIgnoreCase(generateGraphs))
					.withGraphType("png")
					.withGraphTheme(Runtime.GraphTheme.DARK)
					.run(preVoteSteps)
					.generateOverviewGraph();

			LOGGER.info("Execution duration : {}s", (System.currentTimeMillis() - startTime) / 1000);
			LOGGER.info("Maximum memory used : {}Mo", getPeakMemUsage() / 1048576);
			LOGGER.info("Stopping Data Integration Service");
		} catch (final URISyntaxException e) {
			LOGGER.error("Error in URI syntax", e);
		} catch (final Exception e) {
			LOGGER.error("an Error occurred", e);
		}
	}

	private long getPeakMemUsage() {
		final List<MemoryPoolMXBean> pools = ManagementFactory.getMemoryPoolMXBeans();
		long total = 0;
		for (final MemoryPoolMXBean memoryPoolMXBean : pools) {
			if (memoryPoolMXBean.getType() == MemoryType.HEAP) {
				final long peakUsed = memoryPoolMXBean.getPeakUsage().getUsed();
				total += peakUsed;
			}
		}
		return total;
	}

	private void ensureTargetDirectoryExists() {
		final File targetDir = new File(targetDirectory);
		if (!targetDir.exists()) {
			boolean success = targetDir.mkdirs();
			checkState(success, "Unable to create the target directory [directory: %s]", targetDirectory);
		}
	}
}
