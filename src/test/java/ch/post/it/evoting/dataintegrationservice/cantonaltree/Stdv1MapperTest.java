/*
 * (c) Copyright 2023 Swiss Post Ltd.
 */
package ch.post.it.evoting.dataintegrationservice.cantonaltree;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;

class Stdv1MapperTest {

	private final Stdv1Mapper mapper = Stdv1Mapper.INSTANCE;

	private final Stdv1CountingCircleLoader stdv1CountingCircleLoader = new Stdv1CountingCircleLoader();
	private final Stdv1DomainOfInfluenceLoader stdv1DomainOfInfluenceLoader = new Stdv1DomainOfInfluenceLoader();

	@Test
	void testLoading() {
		final Iterable<Stdv1DomainOfInfluence> doiIt = stdv1DomainOfInfluenceLoader.loadDomainOfInfluence(
				"./src/test/resources/Stdv1MapperTest/DomainOfInfluenceTreeExport_20161004120430_doi_stdv1.csv");

		final List<Stdv1DomainOfInfluence> dois = new ArrayList<>();
		doiIt.iterator().forEachRemaining(dois::add);

		final Iterable<Stdv1CountingCircle> ccIt = stdv1CountingCircleLoader.loadCountingCircle(
				"./src/test/resources/Stdv1MapperTest/CountingCirclesExport_20161004120430_cc_stdv1.csv");
		final List<Stdv1CountingCircle> ccs = new ArrayList<>();
		ccIt.iterator().forEachRemaining(ccs::add);

		final Optional<CantonalTree> tree = mapper.convert(dois, ccs);

		assertEquals(190, tree.get().getAllDomainOfInfluences().size());
		assertEquals(681, tree.get().getAllDomainOfInfluences().stream().flatMap(d -> d.getDomainOfInfluenceIds().stream()).count());
		assertEquals(6, tree.get().getAllDomainOfInfluences().stream()
				.flatMap(d -> d.getDomainOfInfluenceIds().stream())
				.filter(i -> i.getParent() == null)
				.count());
		assertEquals(178, tree.get().getAllCountingCircles().size());
	}

	@Test
	void testSplitting() {
		final String[] splitted = mapper.splitCode("FR-CH-1");
		assertEquals(3, splitted.length);
		assertEquals("FR", splitted[0]);
		assertEquals("CH", splitted[1]);
		assertEquals("1", splitted[2]);
	}

	@Test
	void testConvertDomainOfInfluence() {
		final CantonalTree cantonalTree = new CantonalTree();

		final Stdv1DomainOfInfluence doidoiStdv1 = new Stdv1DomainOfInfluence();
		doidoiStdv1.setId("123");
		doidoiStdv1.setDescription("desc");
		doidoiStdv1.setCantonCode("NE");
		doidoiStdv1.setCode("NE-CT-2");
		doidoiStdv1.setParentCode("NE-CH-1");
		doidoiStdv1.setParentId("456");

		final DomainOfInfluence result = mapper.convert(cantonalTree, doidoiStdv1);

		assertEquals("NE", result.getCantonAbbreviation());
		assertEquals("CT", result.getType());
		assertEquals("2", result.getLocalId());
		assertEquals(1, result.getDomainOfInfluenceIds().size());
		assertEquals("123", result.getDomainOfInfluenceIds().get(0).getId());
		assertEquals("456", result.getDomainOfInfluenceIds().get(0).getParent().getId());
		assertEquals("1", result.getDomainOfInfluenceIds().get(0).getParent().getRelatedDomainOfInfluence().getLocalId());
		assertEquals("CH", result.getDomainOfInfluenceIds().get(0).getParent().getRelatedDomainOfInfluence().getType());
		assertEquals("NE", result.getDomainOfInfluenceIds().get(0).getParent().getRelatedDomainOfInfluence().getCantonAbbreviation());
		assertNull(result.getDomainOfInfluenceIds().get(0).getParent().getParent());
	}
}
