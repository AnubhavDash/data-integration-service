/*
 * (c) Copyright 2026 Swiss Post Ltd.
 */
package ch.post.it.evoting.dataintegrationservice.register;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import ch.ech.xmlns.ech_0045._6.VotingPersonType;
import ch.post.it.evoting.dataintegrationservice.domain.VoterCountingCircle;
import ch.post.it.evoting.dataintegrationservice.domain.VoterDomainOfInfluenceInfo;
import ch.post.it.evoting.dataintegrationservice.domain.VoterTypeWithRole;

@DisplayName("ECHv42FileRegisterLoader loading")
class ECHv42FileRegisterLoaderTest {

	private static ECHv42FileRegisterLoader ecHv42FileRegisterLoader;

	@BeforeAll
	static void setUpAll() {
		ecHv42FileRegisterLoader = new ECHv42FileRegisterLoader();
	}

	@Nested
	@DisplayName("loadECH0045v43 with")
	class LoadECH0045v43 {

		@Test
		@DisplayName("null file location throws NullPointerException")
		void nullParams() {
			assertThrows(NullPointerException.class, () -> ecHv42FileRegisterLoader.loadECH0045v42(null));
		}

		@Test
		@DisplayName("non-existent file throws IllegalArgumentException")
		void nonExistentFile() {
			final String nonExistentFile = "non-existent-file.xml";
			final IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
					() -> ecHv42FileRegisterLoader.loadECH0045v42(nonExistentFile));
			assertEquals("Unable to load eCH-0045 file. [location: %s]".formatted(nonExistentFile), exception.getMessage());
		}

		@Test
		@DisplayName("an eCH-0045 v4.2 file gives expected voters")
		void loadECHFile() {
			final Iterable<VotingPersonType> result = ecHv42FileRegisterLoader.loadECH0045v42(
					"./src/test/resources/ECHv42FileRegisterLoaderTest/file1.ech0045v4_2.one.xml");

			final List<VotingPersonType> voters = new ArrayList<>();
			result.iterator().forEachRemaining(voters::add);

			assertEquals(1, voters.size());
			assertNotNull(voters.getFirst().getPerson());
		}
	}

	@Nested
	@DisplayName("checkConsistency with")
	class CheckConsistency {

		private VoterTypeWithRole voter;

		@BeforeEach
		void setUp() {
			voter = new VoterTypeWithRole();
			voter.setVoterIdentification(UUID.randomUUID().toString());

			final VoterCountingCircle countingCircle = new VoterCountingCircle();
			countingCircle.setId(UUID.randomUUID().toString());
			countingCircle.setName(UUID.randomUUID().toString());

			final VoterDomainOfInfluenceInfo doiInfo = new VoterDomainOfInfluenceInfo();
			doiInfo.setCountingCircle(countingCircle);

			voter.setDomainOfInfluenceInfos(List.of(doiInfo));
		}

		@Test
		@DisplayName("valid voter returns the voter")
		void validVoter() {
			final VoterTypeWithRole result = ecHv42FileRegisterLoader.checkConsistency(voter);
			assertEquals(voter, result);
		}

		@Test
		@DisplayName("null voter throws NullPointerException")
		void nullParams() {
			assertThrows(NullPointerException.class, () -> ecHv42FileRegisterLoader.checkConsistency(null));
		}

		@Test
		@DisplayName("voter with missing counting circle throws IllegalStateException")
		void missingCountingCircle() {
			final VoterDomainOfInfluenceInfo doiInfoWithoutCountingCircle = new VoterDomainOfInfluenceInfo();
			voter.setDomainOfInfluenceInfos(List.of(doiInfoWithoutCountingCircle));

			final IllegalStateException exception = assertThrows(IllegalStateException.class, () -> ecHv42FileRegisterLoader.checkConsistency(voter));
			assertEquals("The voter must have all its counting circles defined. [voterId: %s]".formatted(voter.getVoterIdentification()),
					exception.getMessage());
		}

		@Test
		@DisplayName("voter with empty domain of influence infos does not throw")
		void emptyDomainOfInfluenceInfos() {
			voter.setDomainOfInfluenceInfos(List.of());
			assertDoesNotThrow(() -> ecHv42FileRegisterLoader.checkConsistency(voter));
		}
	}

}
