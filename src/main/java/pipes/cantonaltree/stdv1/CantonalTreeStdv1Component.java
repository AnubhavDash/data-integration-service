/*
 * (c) Copyright 2023 Swiss Post Ltd.
 */
package pipes.cantonaltree.stdv1;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import org.springframework.stereotype.Component;

import ch.post.it.evoting.dataintegrationservice.cantonaltree.CantonalTree;
import ch.post.it.evoting.dataintegrationservice.cantonaltree.Stdv1CountingCircle;
import ch.post.it.evoting.dataintegrationservice.cantonaltree.Stdv1CountingCircleLoader;
import ch.post.it.evoting.dataintegrationservice.cantonaltree.Stdv1DomainOfInfluence;
import ch.post.it.evoting.dataintegrationservice.cantonaltree.Stdv1DomainOfInfluenceLoader;
import ch.post.it.evoting.dataintegrationservice.cantonaltree.Stdv1Mapper;

import reactor.util.function.Tuple2;

@Component
public class CantonalTreeStdv1Component {

	private final Stdv1CountingCircleLoader stdv1CountingCircleLoader;
	private final Stdv1DomainOfInfluenceLoader stdv1DomainOfInfluenceLoader;

	public CantonalTreeStdv1Component(final Stdv1CountingCircleLoader stdv1CountingCircleLoader,
			final Stdv1DomainOfInfluenceLoader stdv1DomainOfInfluenceLoader) {
		this.stdv1CountingCircleLoader = stdv1CountingCircleLoader;
		this.stdv1DomainOfInfluenceLoader = stdv1DomainOfInfluenceLoader;
	}

	public Function<Tuple2<List<Stdv1DomainOfInfluence>, List<Stdv1CountingCircle>>, Optional<CantonalTree>> buildCantonTree() {
		return tuple -> Stdv1Mapper.INSTANCE.convert(tuple.getT1(), tuple.getT2());
	}

	public Function<String, Iterable<Stdv1CountingCircle>> loadStdv1CountingCircle() {
		return stdv1CountingCircleLoader::loadCountingCircle;
	}

	public Function<String, Iterable<Stdv1DomainOfInfluence>> loadStdv1DomainOfInfluence() {
		return stdv1DomainOfInfluenceLoader::loadDomainOfInfluence;
	}
}
