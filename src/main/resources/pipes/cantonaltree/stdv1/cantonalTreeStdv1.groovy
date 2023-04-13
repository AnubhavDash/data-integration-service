/*
 * (c) Copyright 2023 Swiss Post Ltd.
 */
package pipes.cantonaltree.stdv1

import ch.post.it.evoting.dataintegrationservice.DataIntegrationServiceApplication

import static li.chee.reactive.plumber.Plumbing.from
import static li.chee.reactive.plumber.Plumbing.pipe
import static pipes.Tools.fileType
import static reactor.core.publisher.Flux.zip

def cantonalTreeStdv1Component = DataIntegrationServiceApplication.context.getBean(CantonalTreeStdv1Component.class)

def stdv1vCountingCircles = pipe {
	from CantonalTreeStdv1.inputPhase.files \
	filter fileType("cc_stdv1") \
	flatMapIterable cantonalTreeStdv1Component.loadStdv1CountingCircle() \
	collectList()

}

def stdv1DomainOfInfluences = pipe {
	from CantonalTreeStdv1.inputPhase.files \
	filter fileType("doi_stdv1") \
	flatMapIterable cantonalTreeStdv1Component.loadStdv1DomainOfInfluence() \
	collectList()

}

def cantonalTree = pipe {
	from zip(stdv1DomainOfInfluences, stdv1vCountingCircles) \
    map cantonalTreeStdv1Component.buildCantonTree() \

}

CantonalTreeStdv1.exports.cantonalTree = cantonalTree
