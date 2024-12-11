Feature: Contest Phase

  Background:

    # PARAMETERS

    Given user parameters named "my params"
    And user parameters "my params" has contest default language "DE"
    And user parameters "my params" has eVoting date from "2045-04-12 00:00:00"
    And user parameters "my params" has eVoting date to "2045-05-22 00:00:00"
    And user parameters "my params" has default incumbent
    And user parameters "my params" has defaultDisplayCandidateLineStyle "DEFAULT"
    And user parameters "my params" has defaultDisplayListLineStyle "DEFAULT"

    # PARAMETERS - writeIns and accumulation
    And user parameters "my params" has election named "election 1"
    And election "my params"."election 1" has id "election-A-id"
    And election "my params"."election 1" has accumulation 1
    And user parameters "my params" has election named "election 2"
    And election "my params"."election 2" has id "election-B-id"
    And election "my params"."election 2" has writeIns "false"
    And election "my params"."election 2" has accumulation 1
    And election "my params"."election 2" has minimalCandidateSelection 0


    # PARAMETERS - default answers
    # PARAMETERS - default answers for type 1
    And user parameters "my params" has default answer type named "answer type 1"
    And default answer type "my params"."answer type 1" has type 1
    And default answer type "my params"."answer type 1" has standardAnswer named "answer 2"
    And standardAnswer "my params"."answer type 1"."answer 2" has answer position 2
    And standardAnswer "my params"."answer type 1"."answer 2" has answer description "DE" "Nein"
    And standardAnswer "my params"."answer type 1"."answer 2" has answer description "FR" "Non"
    And standardAnswer "my params"."answer type 1"."answer 2" has answer description "IT" "No"
    And standardAnswer "my params"."answer type 1"."answer 2" has answer description "RM" "Nein"
    And standardAnswer "my params"."answer type 1"."answer 2" has answer standardAnswerType = "NO"
    And default answer type "my params"."answer type 1" has standardAnswer named "answer 1"
    And standardAnswer "my params"."answer type 1"."answer 1" has answer position 1
    And standardAnswer "my params"."answer type 1"."answer 1" has answer description "DE" "Ja"
    And standardAnswer "my params"."answer type 1"."answer 1" has answer description "FR" "Oui"
    And standardAnswer "my params"."answer type 1"."answer 1" has answer description "IT" "Si"
    And standardAnswer "my params"."answer type 1"."answer 1" has answer description "RM" "Ja"
    And standardAnswer "my params"."answer type 1"."answer 1" has answer standardAnswerType = "YES"
    # PARAMETERS - default answers for type 2
    And user parameters "my params" has default answer type named "answer type 2"
    And default answer type "my params"."answer type 2" has type 2
    And default answer type "my params"."answer type 2" has standardAnswer named "answer 1"
    And standardAnswer "my params"."answer type 2"."answer 1" has answer position 3
    And standardAnswer "my params"."answer type 2"."answer 1" has answer description "DE" "Leer"
    And standardAnswer "my params"."answer type 2"."answer 1" has answer description "FR" "Vide"
    And standardAnswer "my params"."answer type 2"."answer 1" has answer description "IT" "Nullo"
    And standardAnswer "my params"."answer type 2"."answer 1" has answer description "RM" "Leer"
    And standardAnswer "my params"."answer type 2"."answer 1" has answer standardAnswerType = "EMPTY"
    And default answer type "my params"."answer type 2" has standardAnswer named "answer 2"
    And standardAnswer "my params"."answer type 2"."answer 2" has answer position 1
    And standardAnswer "my params"."answer type 2"."answer 2" has answer description "DE" "Ja"
    And standardAnswer "my params"."answer type 2"."answer 2" has answer description "FR" "Oui"
    And standardAnswer "my params"."answer type 2"."answer 2" has answer description "IT" "Si"
    And standardAnswer "my params"."answer type 2"."answer 2" has answer description "RM" "Ja"
    And standardAnswer "my params"."answer type 2"."answer 2" has answer standardAnswerType = "YES"
    And default answer type "my params"."answer type 2" has standardAnswer named "answer 3"
    And standardAnswer "my params"."answer type 2"."answer 3" has answer position 2
    And standardAnswer "my params"."answer type 2"."answer 3" has answer description "DE" "Nein"
    And standardAnswer "my params"."answer type 2"."answer 3" has answer description "FR" "Non"
    And standardAnswer "my params"."answer type 2"."answer 3" has answer description "IT" "No"
    And standardAnswer "my params"."answer type 2"."answer 3" has answer description "RM" "Nein"
    And standardAnswer "my params"."answer type 2"."answer 3" has answer standardAnswerType = "NO"

    # PARAMETERS - specific answers for one of the question
    And user parameters "my params" has question "question variant C"
    And parameter question "my params"."question variant C" has questionIdentification "V-B_B-1_Q-proposal_C"
    And parameter question "my params"."question variant C" has standardAnswer named "answer 1"
    And standardAnswer "my params"."question variant C"."answer 1" has answer position 1
    And standardAnswer "my params"."question variant C"."answer 1" has answer description "DE" "Ja Ja"
    And standardAnswer "my params"."question variant C"."answer 1" has answer description "FR" "Oui Oui"
    And standardAnswer "my params"."question variant C"."answer 1" has answer description "IT" "Si Si"
    And standardAnswer "my params"."question variant C"."answer 1" has answer description "RM" "Ja Ja"
    And standardAnswer "my params"."question variant C"."answer 1" has answer standardAnswerType = "YES"
    And parameter question "my params"."question variant C" has standardAnswer named "answer 2"
    And standardAnswer "my params"."question variant C"."answer 2" has answer position 2
    And standardAnswer "my params"."question variant C"."answer 2" has answer description "DE" "Nein Nein"
    And standardAnswer "my params"."question variant C"."answer 2" has answer description "FR" "Non Non"
    And standardAnswer "my params"."question variant C"."answer 2" has answer description "IT" "No No"
    And standardAnswer "my params"."question variant C"."answer 2" has answer description "RM" "Nein Nein"
    And standardAnswer "my params"."question variant C"."answer 2" has answer standardAnswerType = "NO"
    # PARAMETERS - specific answers for tiebreak questions
    And user parameters "my params" has question "tiebreak question 1"
    And parameter question "my params"."tiebreak question 1" has questionIdentification "V-B_B-1_TQ-1"
    And parameter question "my params"."tiebreak question 1" has tiebreakAnswer named "answer 1"
    And tiebreakAnswer "my params"."tiebreak question 1"."answer 1" has answer position 1
    And tiebreakAnswer "my params"."tiebreak question 1"."answer 1" has answer questionReference "V-B_B-1_Q-proposal_A"
    And tiebreakAnswer "my params"."tiebreak question 1"."answer 1" has answer description "DE" "I prefer A (DE)"
    And tiebreakAnswer "my params"."tiebreak question 1"."answer 1" has answer description "FR" "I prefer A (FR)"
    And tiebreakAnswer "my params"."tiebreak question 1"."answer 1" has answer description "IT" "I prefer A (IT)"
    And tiebreakAnswer "my params"."tiebreak question 1"."answer 1" has answer description "RM" "I prefer A (RM)"
    And parameter question "my params"."tiebreak question 1" has tiebreakAnswer named "answer 2"
    And tiebreakAnswer "my params"."tiebreak question 1"."answer 2" has answer position 2
    And tiebreakAnswer "my params"."tiebreak question 1"."answer 2" has answer questionReference "V-B_B-1_Q-proposal_B"
    And tiebreakAnswer "my params"."tiebreak question 1"."answer 2" has answer description "DE" "I prefer B (DE)"
    And tiebreakAnswer "my params"."tiebreak question 1"."answer 2" has answer description "FR" "I prefer B (FR)"
    And tiebreakAnswer "my params"."tiebreak question 1"."answer 2" has answer description "IT" "I prefer B (IT)"
    And tiebreakAnswer "my params"."tiebreak question 1"."answer 2" has answer description "RM" "I prefer B (RM)"
    And user parameters "my params" has question "tiebreak question 2"
    And parameter question "my params"."tiebreak question 2" has questionIdentification "V-B_B-1_TQ-2"
    And parameter question "my params"."tiebreak question 2" has tiebreakAnswer named "answer 1"
    And tiebreakAnswer "my params"."tiebreak question 2"."answer 1" has answer position 1
    And tiebreakAnswer "my params"."tiebreak question 2"."answer 1" has answer questionReference "V-B_B-1_Q-proposal_A"
    And tiebreakAnswer "my params"."tiebreak question 2"."answer 1" has answer description "DE" "I prefer A (DE)"
    And tiebreakAnswer "my params"."tiebreak question 2"."answer 1" has answer description "FR" "I prefer A (FR)"
    And tiebreakAnswer "my params"."tiebreak question 2"."answer 1" has answer description "IT" "I prefer A (IT)"
    And tiebreakAnswer "my params"."tiebreak question 2"."answer 1" has answer description "RM" "I prefer A (RM)"
    And parameter question "my params"."tiebreak question 2" has tiebreakAnswer named "answer 2"
    And tiebreakAnswer "my params"."tiebreak question 2"."answer 2" has answer position 2
    And tiebreakAnswer "my params"."tiebreak question 2"."answer 2" has answer questionReference "V-B_B-1_Q-proposal_C"
    And tiebreakAnswer "my params"."tiebreak question 2"."answer 2" has answer description "DE" "I prefer C (DE)"
    And tiebreakAnswer "my params"."tiebreak question 2"."answer 2" has answer description "FR" "I prefer C (FR)"
    And tiebreakAnswer "my params"."tiebreak question 2"."answer 2" has answer description "IT" "I prefer C (IT)"
    And tiebreakAnswer "my params"."tiebreak question 2"."answer 2" has answer description "RM" "I prefer C (RM)"
    And user parameters "my params" has question "tiebreak question 3"
    And parameter question "my params"."tiebreak question 3" has questionIdentification "V-B_B-1_TQ-3"
    And parameter question "my params"."tiebreak question 3" has tiebreakAnswer named "answer 1"
    And tiebreakAnswer "my params"."tiebreak question 3"."answer 1" has answer position 1
    And tiebreakAnswer "my params"."tiebreak question 3"."answer 1" has answer questionReference "V-B_B-1_Q-proposal_B"
    And tiebreakAnswer "my params"."tiebreak question 3"."answer 1" has answer description "DE" "I prefer B (DE)"
    And tiebreakAnswer "my params"."tiebreak question 3"."answer 1" has answer description "FR" "I prefer B (FR)"
    And tiebreakAnswer "my params"."tiebreak question 3"."answer 1" has answer description "IT" "I prefer B (IT)"
    And tiebreakAnswer "my params"."tiebreak question 3"."answer 1" has answer description "RM" "I prefer B (RM)"
    And parameter question "my params"."tiebreak question 3" has tiebreakAnswer named "answer 2"
    And tiebreakAnswer "my params"."tiebreak question 3"."answer 2" has answer position 2
    And tiebreakAnswer "my params"."tiebreak question 3"."answer 2" has answer questionReference "V-B_B-1_Q-proposal_C"
    And tiebreakAnswer "my params"."tiebreak question 3"."answer 2" has answer description "DE" "I prefer C (DE)"
    And tiebreakAnswer "my params"."tiebreak question 3"."answer 2" has answer description "FR" "I prefer C (FR)"
    And tiebreakAnswer "my params"."tiebreak question 3"."answer 2" has answer description "IT" "I prefer C (IT)"
    And tiebreakAnswer "my params"."tiebreak question 3"."answer 2" has answer description "RM" "I prefer C (RM)"

    # CONTEST
    Given a contest named "my contest"
    And the contest "my contest" has Id="xxxx-yyyy"
    And the contest "my contest" has date "2045-05-30 00:00:00"
    And the contest "my contest" has description "DE" "Test contest (DE)"
    And the contest "my contest" has description "FR" "Test contest (FR)"
    And the contest "my contest" has description "IT" "Test contest (IT)"
    And the contest "my contest" has description "RM" "Test contest (RM)"

    # VOTES
    Given a list of votes named "my votes"

    # VOTE A
    And a vote named "vote A" within "my votes"
    And the vote "my votes"."vote A" has id "vote-A-id"
    And the vote "my votes"."vote A" has domain of influence "DOI-voteA"
    And the vote "my votes"."vote A" has description "DE" "Test vote A (DE)"
    And the vote "my votes"."vote A" has description "FR" "Test vote A (FR)"
    And the vote "my votes"."vote A" has description "IT" "Test vote A (IT)"
    And the vote "my votes"."vote A" has description "RM" "Test vote A (RM)"

    # VOTE A -standard ballot
    Given the vote "my votes"."vote A" has a "standard" ballot "vote A ballot 1"
    And the ballot "my votes"."vote A"."vote A ballot 1" has id "vote-A-ballot-1-id"
    And the ballot "my votes"."vote A"."vote A ballot 1" has description "DE" "vote A ballot 1 description (DE)"
    And the ballot "my votes"."vote A"."vote A ballot 1" has description "FR" "vote A ballot 1 description (FR)"
    And the ballot "my votes"."vote A"."vote A ballot 1" has description "IT" "vote A ballot 1 description (IT)"
    And the ballot "my votes"."vote A"."vote A ballot 1" has description "RM" "vote A ballot 1 description (RM)"
    # VOTE A -standard ballot - simple question
    And the ballot "my votes"."vote A"."vote A ballot 1" has question id  "V-A_B-1_Q-0"
    And the ballot "my votes"."vote A"."vote A ballot 1" has answer type 1
    And the ballot question of ballot "my votes"."vote A"."vote A ballot 1" has info "DE" "Proposal" "Do you accept this proposal (DE)"
    And the ballot question of ballot "my votes"."vote A"."vote A ballot 1" has info "FR" "Proposal" "Do you accept this proposal (FR)"
    And the ballot question of ballot "my votes"."vote A"."vote A ballot 1" has info "IT" "Proposal" "Do you accept this proposal (IT)"
    And the ballot question of ballot "my votes"."vote A"."vote A ballot 1" has info "RM" "Proposal" "Do you accept this proposal (RM)"

    # VOTE B -variant ballot
    And a vote named "vote B" within "my votes"
    And the vote "my votes"."vote B" has id "vote-B-id"
    And the vote "my votes"."vote B" has domain of influence "DOI-voteB"
    And the vote "my votes"."vote B" has description "DE" "Test vote B (DE)"
    And the vote "my votes"."vote B" has description "FR" "Test vote B (FR)"
    And the vote "my votes"."vote B" has description "IT" "Test vote B (IT)"
    And the vote "my votes"."vote B" has description "RM" "Test vote B (RM)"
    # VOTE B -variant ballot
    Given the vote "my votes"."vote B" has a "variant" ballot "vote B ballot 1"
    And the ballot "my votes"."vote B"."vote B ballot 1" has id "vote-B-ballot-1-id"
    And the ballot "my votes"."vote B"."vote B ballot 1" has description "DE" "vote B ballot 1 description (DE)"
    And the ballot "my votes"."vote B"."vote B ballot 1" has description "FR" "vote B ballot 1 description (FR)"
    And the ballot "my votes"."vote B"."vote B ballot 1" has description "IT" "vote B ballot 1 description (IT)"
    And the ballot "my votes"."vote B"."vote B ballot 1" has description "RM" "vote B ballot 1 description (RM)"
    # VOTE B -variant ballot - question with answer type 2 (3 answers)
    And the ballot "my votes"."vote B"."vote B ballot 1" has variant ballot standard question "vote B ballot 1 question 1"
    And the standard question "my votes"."vote B"."vote B ballot 1"."vote B ballot 1 question 1" has id "V-B_B-1_Q-proposal_A"
    And the standard question "my votes"."vote B"."vote B ballot 1"."vote B ballot 1 question 1" has position 3
    And the standard question "my votes"."vote B"."vote B ballot 1"."vote B ballot 1 question 1" has answer type 2
    And the standard question "my votes"."vote B"."vote B ballot 1"."vote B ballot 1 question 1" has info "DE" "Proposal A" "Do you accept proposal A (DE)"
    And the standard question "my votes"."vote B"."vote B ballot 1"."vote B ballot 1 question 1" has info "FR" "Proposal A" "Do you accept proposal A (FR)"
    And the standard question "my votes"."vote B"."vote B ballot 1"."vote B ballot 1 question 1" has info "IT" "Proposal A" "Do you accept proposal A (IT)"
    And the standard question "my votes"."vote B"."vote B ballot 1"."vote B ballot 1 question 1" has info "RM" "Proposal A" "Do you accept proposal A (RM)"
    # VOTE B -variant ballot - question with answer type 1 (2 answers)
    And the ballot "my votes"."vote B"."vote B ballot 1" has variant ballot standard question "vote B ballot 1 question 2"
    And the standard question "my votes"."vote B"."vote B ballot 1"."vote B ballot 1 question 2" has id "V-B_B-1_Q-proposal_B"
    And the standard question "my votes"."vote B"."vote B ballot 1"."vote B ballot 1 question 2" has position 2
    And the standard question "my votes"."vote B"."vote B ballot 1"."vote B ballot 1 question 2" has answer type 1
    And the standard question "my votes"."vote B"."vote B ballot 1"."vote B ballot 1 question 2" has info "DE" "Proposal B" "Do you accept proposal B (DE)"
    And the standard question "my votes"."vote B"."vote B ballot 1"."vote B ballot 1 question 2" has info "FR" "Proposal B" "Do you accept proposal B (FR)"
    And the standard question "my votes"."vote B"."vote B ballot 1"."vote B ballot 1 question 2" has info "IT" "Proposal B" "Do you accept proposal B (IT)"
    And the standard question "my votes"."vote B"."vote B ballot 1"."vote B ballot 1 question 2" has info "RM" "Proposal B" "Do you accept proposal B (RM)"
    # VOTE B -variant ballot - question with answer type 1 (2 answers - and override in parameters)
    And the ballot "my votes"."vote B"."vote B ballot 1" has variant ballot standard question "vote B ballot 1 question 3"
    And the standard question "my votes"."vote B"."vote B ballot 1"."vote B ballot 1 question 3" has id "V-B_B-1_Q-proposal_C"
    And the standard question "my votes"."vote B"."vote B ballot 1"."vote B ballot 1 question 3" has position 1
    And the standard question "my votes"."vote B"."vote B ballot 1"."vote B ballot 1 question 3" has answer type 1
    And the standard question "my votes"."vote B"."vote B ballot 1"."vote B ballot 1 question 3" has info "DE" "Proposal C" "Do you accept proposal C (DE)"
    And the standard question "my votes"."vote B"."vote B ballot 1"."vote B ballot 1 question 3" has info "FR" "Proposal C" "Do you accept proposal C (FR)"
    And the standard question "my votes"."vote B"."vote B ballot 1"."vote B ballot 1 question 3" has info "IT" "Proposal C" "Do you accept proposal C (IT)"
    And the standard question "my votes"."vote B"."vote B ballot 1"."vote B ballot 1 question 3" has info "RM" "Proposal C" "Do you accept proposal C (RM)"
    # VOTE B -variant ballot - tiebreak question 1 with mandatory specific answers in parameters
    And the ballot "my votes"."vote B"."vote B ballot 1" has variant ballot tiebreak question "vote B ballot 1 tb question 1"
    And the tiebreak question "my votes"."vote B"."vote B ballot 1"."vote B ballot 1 tb question 1" has id "V-B_B-1_TQ-1"
    And the tiebreak question "my votes"."vote B"."vote B ballot 1"."vote B ballot 1 tb question 1" has referenceQuestion1 "V-B_B-1_Q-proposal_A"
    And the tiebreak question "my votes"."vote B"."vote B ballot 1"."vote B ballot 1 tb question 1" has referenceQuestion2 "V-B_B-1_Q-proposal_B"
    And the tiebreak question "my votes"."vote B"."vote B ballot 1"."vote B ballot 1 tb question 1" has position 4
    And the tiebreak question "my votes"."vote B"."vote B ballot 1"."vote B ballot 1 tb question 1" has answer type 4
    And the tiebreak question "my votes"."vote B"."vote B ballot 1"."vote B ballot 1 tb question 1" has info "DE" "Tie-break question AB" "Do you prefer A or B (DE)"
    And the tiebreak question "my votes"."vote B"."vote B ballot 1"."vote B ballot 1 tb question 1" has info "FR" "Tie-break question AB" "Do you prefer A or B (FR)"
    And the tiebreak question "my votes"."vote B"."vote B ballot 1"."vote B ballot 1 tb question 1" has info "IT" "Tie-break question AB" "Do you prefer A or B (IT)"
    And the tiebreak question "my votes"."vote B"."vote B ballot 1"."vote B ballot 1 tb question 1" has info "RM" "Tie-break question AB" "Do you prefer A or B (RM)"
   # VOTE B -variant ballot - tiebreak question 2 with mandatory specific answers in parameters
    And the ballot "my votes"."vote B"."vote B ballot 1" has variant ballot tiebreak question "vote B ballot 1 tb question 2"
    And the tiebreak question "my votes"."vote B"."vote B ballot 1"."vote B ballot 1 tb question 2" has id "V-B_B-1_TQ-2"
    And the tiebreak question "my votes"."vote B"."vote B ballot 1"."vote B ballot 1 tb question 1" has referenceQuestion1 "V-B_B-1_Q-proposal_A"
    And the tiebreak question "my votes"."vote B"."vote B ballot 1"."vote B ballot 1 tb question 1" has referenceQuestion2 "V-B_B-1_Q-proposal_C"
    And the tiebreak question "my votes"."vote B"."vote B ballot 1"."vote B ballot 1 tb question 2" has position 6
    And the tiebreak question "my votes"."vote B"."vote B ballot 1"."vote B ballot 1 tb question 2" has answer type 4
    And the tiebreak question "my votes"."vote B"."vote B ballot 1"."vote B ballot 1 tb question 2" has info "DE" "Tie-break question AC" "Do you prefer A or C (DE)"
    And the tiebreak question "my votes"."vote B"."vote B ballot 1"."vote B ballot 1 tb question 2" has info "FR" "Tie-break question AC" "Do you prefer A or C (FR)"
    And the tiebreak question "my votes"."vote B"."vote B ballot 1"."vote B ballot 1 tb question 2" has info "IT" "Tie-break question AC" "Do you prefer A or C (IT)"
    And the tiebreak question "my votes"."vote B"."vote B ballot 1"."vote B ballot 1 tb question 2" has info "RM" "Tie-break question AC" "Do you prefer A or C (RM)"
   # VOTE B -variant ballot - tiebreak question 3 with mandatory specific answers in parameters
    And the ballot "my votes"."vote B"."vote B ballot 1" has variant ballot tiebreak question "vote B ballot 1 tb question 3"
    And the tiebreak question "my votes"."vote B"."vote B ballot 1"."vote B ballot 1 tb question 3" has id "V-B_B-1_TQ-3"
    And the tiebreak question "my votes"."vote B"."vote B ballot 1"."vote B ballot 1 tb question 1" has referenceQuestion1 "V-B_B-1_Q-proposal_B"
    And the tiebreak question "my votes"."vote B"."vote B ballot 1"."vote B ballot 1 tb question 1" has referenceQuestion2 "V-B_B-1_Q-proposal_C"
    And the tiebreak question "my votes"."vote B"."vote B ballot 1"."vote B ballot 1 tb question 3" has position 5
    And the tiebreak question "my votes"."vote B"."vote B ballot 1"."vote B ballot 1 tb question 3" has answer type 4
    And the tiebreak question "my votes"."vote B"."vote B ballot 1"."vote B ballot 1 tb question 3" has info "DE" "Tie-break question BC" "Do you prefer B or C (DE)"
    And the tiebreak question "my votes"."vote B"."vote B ballot 1"."vote B ballot 1 tb question 3" has info "FR" "Tie-break question BC" "Do you prefer B or C (FR)"
    And the tiebreak question "my votes"."vote B"."vote B ballot 1"."vote B ballot 1 tb question 3" has info "IT" "Tie-break question BC" "Do you prefer B or C (IT)"
    And the tiebreak question "my votes"."vote B"."vote B ballot 1"."vote B ballot 1 tb question 3" has info "RM" "Tie-break question BC" "Do you prefer B or C (RM)"

    # VOTE B -variant ballot - tiebreak question 4 with an answer type 1
    #And the ballot "my votes"."vote B"."vote B ballot 1" has variant ballot tiebreak question "vote B ballot 1 tb question 4"
    #And the tiebreak question "my votes"."vote B"."vote B ballot 1"."vote B ballot 1 tb question 4" has id "V-B_B-1_TQ-4"
    #And the tiebreak question "my votes"."vote B"."vote B ballot 1"."vote B ballot 1 tb question 4" has position 7
    #And the tiebreak question "my votes"."vote B"."vote B ballot 1"."vote B ballot 1 tb question 4" has answer type 1
    #And the tiebreak question "my votes"."vote B"."vote B ballot 1"."vote B ballot 1 tb question 4" has info "DE" "In this case, do you agree ? (DE)"
    #And the tiebreak question "my votes"."vote B"."vote B ballot 1"."vote B ballot 1 tb question 4" has info "FR" "In this case, do you agree ? (FR)"
    #And the tiebreak question "my votes"."vote B"."vote B ballot 1"."vote B ballot 1 tb question 4" has info "IT" "In this case, do you agree ? (IT)"
    #And the tiebreak question "my votes"."vote B"."vote B ballot 1"."vote B ballot 1 tb question 4" has info "RM" "In this case, do you agree ? (RM)"

    # ELECTIONS
    Given a list of elections named "my elections"
    And an election named "election A" within "my elections"
    And the election "my elections"."election A" has id "election-A-id"
    And the election "my elections"."election A" has domain of influence "DOI-electionA"
    And the election "my elections"."election A" has description "DE" "Test election A (DE)"
    And the election "my elections"."election A" has description "FR" "Test election A (FR)"
    And the election "my elections"."election A" has description "IT" "Test election A (IT)"
    And the election "my elections"."election A" has description "RM" "Test election A (RM)"
    And an election named "election B" within "my elections"
    And the election "my elections"."election B" has id "election-B-id"
    And the election "my elections"."election B" has domain of influence "DOI-electionB"
    And the election "my elections"."election B" has description "DE" "Test election B (DE)"
    And the election "my elections"."election B" has description "FR" "Test election B (FR)"
    And the election "my elections"."election B" has description "IT" "Test election B (IT)"
    And the election "my elections"."election B" has description "RM" "Test election B (RM)"

    Given a candidate named "candidate 1 for A" within "my elections"."election A"
    And candidate "my elections"."election A"."candidate 1 for A" has id "candidate-1-for-A-id"
    And candidate "my elections"."election A"."candidate 1 for A" has name "first-name-1" "last-name-1"
    And candidate "my elections"."election A"."candidate 1 for A" has referenceOnPosition "1-for-A"
    And candidate "my elections"."election A"."candidate 1 for A" has text "DE" "Candidate 1 for A text (DE)"
    And candidate "my elections"."election A"."candidate 1 for A" has text "FR" "Candidate 1 for A text (FR)"
    And candidate "my elections"."election A"."candidate 1 for A" has text "IT" "Candidate 1 for A text (IT)"
    And candidate "my elections"."election A"."candidate 1 for A" has text "RM" "Candidate 1 for A text (RM)"
    And candidate "my elections"."election A"."candidate 1 for A" has occupational title "DE" "Candidate 1 for A title (DE)"
    And candidate "my elections"."election A"."candidate 1 for A" has occupational title "FR" "Candidate 1 for A title (FR)"
    And candidate "my elections"."election A"."candidate 1 for A" has occupational title "IT" "Candidate 1 for A title (IT)"
    And candidate "my elections"."election A"."candidate 1 for A" has occupational title "RM" "Candidate 1 for A title (RM)"
    And candidate "my elections"."election A"."candidate 1 for A" has eligibility "explicit"

    Given a candidate named "candidate 2 for A" within "my elections"."election A"
    And candidate "my elections"."election A"."candidate 2 for A" has id "candidate-2-for-A-id"
    And candidate "my elections"."election A"."candidate 2 for A" has name "first-name-2" "last-name-2"
    And candidate "my elections"."election A"."candidate 2 for A" has referenceOnPosition "2-for-A"
    And candidate "my elections"."election A"."candidate 2 for A" has text "DE" "Candidate 2 for A text (DE)"
    And candidate "my elections"."election A"."candidate 2 for A" has text "FR" "Candidate 2 for A text (FR)"
    And candidate "my elections"."election A"."candidate 2 for A" has text "IT" "Candidate 2 for A text (IT)"
    And candidate "my elections"."election A"."candidate 2 for A" has text "RM" "Candidate 2 for A text (RM)"
    And candidate "my elections"."election A"."candidate 2 for A" has occupational title "DE" "Candidate 2 for A title (DE)"
    And candidate "my elections"."election A"."candidate 2 for A" has occupational title "FR" "Candidate 2 for A title (FR)"
    And candidate "my elections"."election A"."candidate 2 for A" has occupational title "IT" "Candidate 2 for A title (IT)"
    And candidate "my elections"."election A"."candidate 2 for A" has occupational title "RM" "Candidate 2 for A title (RM)"
    And candidate "my elections"."election A"."candidate 2 for A" has eligibility "explicit"

    Given a candidate named "candidate 3 for A" within "my elections"."election A"
    And candidate "my elections"."election A"."candidate 3 for A" has id "candidate-3-for-A-id"
    And candidate "my elections"."election A"."candidate 3 for A" has name "first-name-3" "last-name-3"
    And candidate "my elections"."election A"."candidate 3 for A" has referenceOnPosition "3-for-A"
    And candidate "my elections"."election A"."candidate 3 for A" has text "DE" "candidate 3 for A text (DE)"
    And candidate "my elections"."election A"."candidate 3 for A" has text "FR" "candidate 3 for A text (FR)"
    And candidate "my elections"."election A"."candidate 3 for A" has text "IT" "candidate 3 for A text (IT)"
    And candidate "my elections"."election A"."candidate 3 for A" has text "RM" "candidate 3 for A text (RM)"
    And candidate "my elections"."election A"."candidate 3 for A" has occupational title "DE" "candidate 3 for A title (DE)"
    And candidate "my elections"."election A"."candidate 3 for A" has occupational title "FR" "candidate 3 for A title (FR)"
    And candidate "my elections"."election A"."candidate 3 for A" has occupational title "IT" "candidate 3 for A title (IT)"
    And candidate "my elections"."election A"."candidate 3 for A" has occupational title "RM" "candidate 3 for A title (RM)"
    And candidate "my elections"."election A"."candidate 3 for A" has eligibility "explicit"

    Given a candidate named "candidate 4 for A" within "my elections"."election A"
    And candidate "my elections"."election A"."candidate 4 for A" has id "candidate-4-for-A-id"
    And candidate "my elections"."election A"."candidate 4 for A" has name "first-name-4" "last-name-4"
    And candidate "my elections"."election A"."candidate 4 for A" has referenceOnPosition "4-for-A"
    And candidate "my elections"."election A"."candidate 4 for A" has text "DE" "candidate 4 for A text (DE)"
    And candidate "my elections"."election A"."candidate 4 for A" has text "FR" "candidate 4 for A text (FR)"
    And candidate "my elections"."election A"."candidate 4 for A" has text "IT" "candidate 4 for A text (IT)"
    And candidate "my elections"."election A"."candidate 4 for A" has text "RM" "candidate 4 for A text (RM)"
    And candidate "my elections"."election A"."candidate 4 for A" has occupational title "DE" "candidate 4 for A title (DE)"
    And candidate "my elections"."election A"."candidate 4 for A" has occupational title "FR" "candidate 4 for A title (FR)"
    And candidate "my elections"."election A"."candidate 4 for A" has occupational title "IT" "candidate 4 for A title (IT)"
    And candidate "my elections"."election A"."candidate 4 for A" has occupational title "RM" "candidate 4 for A title (RM)"
    And candidate "my elections"."election A"."candidate 4 for A" has eligibility "explicit"

    Given a candidate named "candidate 5 for A" within "my elections"."election A"
    And candidate "my elections"."election A"."candidate 5 for A" has id "candidate-5-for-A-id"
    And candidate "my elections"."election A"."candidate 5 for A" has name "first-name-5" "last-name-5"
    And candidate "my elections"."election A"."candidate 5 for A" has referenceOnPosition "5-for-A"
    And candidate "my elections"."election A"."candidate 5 for A" has text "DE" "candidate 5 for A text (DE)"
    And candidate "my elections"."election A"."candidate 5 for A" has text "FR" "candidate 5 for A text (FR)"
    And candidate "my elections"."election A"."candidate 5 for A" has text "IT" "candidate 5 for A text (IT)"
    And candidate "my elections"."election A"."candidate 5 for A" has text "RM" "candidate 5 for A text (RM)"
    And candidate "my elections"."election A"."candidate 5 for A" has occupational title "DE" "candidate 5 for A title (DE)"
    And candidate "my elections"."election A"."candidate 5 for A" has occupational title "FR" "candidate 5 for A title (FR)"
    And candidate "my elections"."election A"."candidate 5 for A" has occupational title "IT" "candidate 5 for A title (IT)"
    And candidate "my elections"."election A"."candidate 5 for A" has occupational title "RM" "candidate 5 for A title (RM)"
    And candidate "my elections"."election A"."candidate 5 for A" has eligibility "explicit"

    Given a list named "list 1 for A" within "my elections"."election A"
    And list "my elections"."election A"."list 1 for A" has id "list-1-for-election-A-id"
    And list "my elections"."election A"."list 1 for A" has indentureNumber "5"
    And list "my elections"."election A"."list 1 for A" has listOrderOfPrecedence 1
    And list "my elections"."election A"."list 1 for A" has description "DE" "list 1 for election A (DE)"
    And list "my elections"."election A"."list 1 for A" has description "FR" "list 1 for election A (FR)"
    And list "my elections"."election A"."list 1 for A" has description "IT" "list 1 for election A (IT)"
    And list "my elections"."election A"."list 1 for A" has description "RM" "list 1 for election A (RM)"

    Given a list named "list 2 for A" within "my elections"."election A"
    And list "my elections"."election A"."list 2 for A" has id "list-2-for-election-A-id"
    And list "my elections"."election A"."list 2 for A" has indentureNumber "4"
    And list "my elections"."election A"."list 2 for A" has listOrderOfPrecedence 2
    And list "my elections"."election A"."list 2 for A" has description "DE" "list 2 for election A (DE)"
    And list "my elections"."election A"."list 2 for A" has description "FR" "list 2 for election A (FR)"
    And list "my elections"."election A"."list 2 for A" has description "IT" "list 2 for election A (IT)"
    And list "my elections"."election A"."list 2 for A" has description "RM" "list 2 for election A (RM)"

    Given a list named "list 3 for A" within "my elections"."election A"
    And list "my elections"."election A"."list 3 for A" has id "list-3-for-election-A-id"
    And list "my elections"."election A"."list 3 for A" has indentureNumber "3"
    And list "my elections"."election A"."list 3 for A" has listOrderOfPrecedence 3
    And list "my elections"."election A"."list 3 for A" has description "DE" "list 3 for election A (DE)"
    And list "my elections"."election A"."list 3 for A" has description "FR" "list 3 for election A (FR)"
    And list "my elections"."election A"."list 3 for A" has description "IT" "list 3 for election A (IT)"
    And list "my elections"."election A"."list 3 for A" has description "RM" "list 3 for election A (RM)"

    Given the empty list within "my elections"."election A"
    And empty list "my elections"."election A" has id "empty-list-for-election-A-id"
    And empty list "my elections"."election A" has indentureNumber "99"
    And empty list "my elections"."election A" has listOrderOfPrecedence 99
    And empty list "my elections"."election A" has description "DE" "empty list for election A (DE)"
    And empty list "my elections"."election A" has description "FR" "empty list for election A (FR)"
    And empty list "my elections"."election A" has description "IT" "empty list for election A (IT)"
    And empty list "my elections"."election A" has description "RM" "empty list for election A (RM)"

    Given a list-union named "list-union 1" within "my elections"."election A"
    And list-union  "my elections"."election A"."list-union 1" has id "list-union-1-id"
    And list-union  "my elections"."election A"."list-union 1" has type 1
    And list-union  "my elections"."election A"."list-union 1" has description "DE" "list-union-1 text (DE)"
    And list-union  "my elections"."election A"."list-union 1" has description "FR" "list-union-1 text (FR)"
    And list-union  "my elections"."election A"."list-union 1" has description "IT" "list-union-1 text (IT)"
    And list-union  "my elections"."election A"."list-union 1" has description "RM" "list-union-1 text (RM)"
    And list-union  "my elections"."election A"."list-union 1" has referenced list "list-2-for-election-A-id"
    And list-union  "my elections"."election A"."list-union 1" has referenced list "list-3-for-election-A-id"

    Given a list-union named "list-union 2" within "my elections"."election A"
    And list-union  "my elections"."election A"."list-union 2" has id "list-union-2-id"
    And list-union  "my elections"."election A"."list-union 2" has type 2
    And list-union  "my elections"."election A"."list-union 2" has description "DE" "list-union-2 text (DE)"
    And list-union  "my elections"."election A"."list-union 2" has description "FR" "list-union-2 text (FR)"
    And list-union  "my elections"."election A"."list-union 2" has description "IT" "list-union-2 text (IT)"
    And list-union  "my elections"."election A"."list-union 2" has description "RM" "list-union-2 text (RM)"
    And list-union  "my elections"."election A"."list-union 2" has referenced list "list-1-for-election-A-id"
    And list-union  "my elections"."election A"."list-union 2" has referenced list "list-3-for-election-A-id"

    And a position named "pos 1 list 1 for A" within "my elections"."election A"."list 1 for A"
    And a position named "pos 2 list 1 for A" within "my elections"."election A"."list 1 for A"
    And a position named "pos 3 list 1 for A" within "my elections"."election A"."list 1 for A"
    And a position named "pos 4 list 1 for A" within "my elections"."election A"."list 1 for A"

    And position "my elections"."election A"."list 1 for A"."pos 1 list 1 for A" has id "candidate-2-for-A-id"
    And position "my elections"."election A"."list 1 for A"."pos 1 list 1 for A" has position in list 1
    And position "my elections"."election A"."list 1 for A"."pos 1 list 1 for A" has text "DE" "candidate 2 for A list text (DE)"
    And position "my elections"."election A"."list 1 for A"."pos 1 list 1 for A" has text "FR" "candidate 2 for A list text (FR)"
    And position "my elections"."election A"."list 1 for A"."pos 1 list 1 for A" has text "IT" "candidate 2 for A list text (IT)"
    And position "my elections"."election A"."list 1 for A"."pos 1 list 1 for A" has text "RM" "candidate 2 for A list text (RM)"

    And position "my elections"."election A"."list 1 for A"."pos 2 list 1 for A" has id "candidate-1-for-A-id"
    And position "my elections"."election A"."list 1 for A"."pos 2 list 1 for A" has position in list 2
    And position "my elections"."election A"."list 1 for A"."pos 2 list 1 for A" has text "DE" "candidate 1 for A list text (DE)"
    And position "my elections"."election A"."list 1 for A"."pos 2 list 1 for A" has text "FR" "candidate 1 for A list text (FR)"
    And position "my elections"."election A"."list 1 for A"."pos 2 list 1 for A" has text "IT" "candidate 1 for A list text (IT)"
    And position "my elections"."election A"."list 1 for A"."pos 2 list 1 for A" has text "RM" "candidate 1 for A list text (RM)"

    And position "my elections"."election A"."list 1 for A"."pos 3 list 1 for A" has id "candidate-4-for-A-id"
    And position "my elections"."election A"."list 1 for A"."pos 3 list 1 for A" has position in list 3
    And position "my elections"."election A"."list 1 for A"."pos 3 list 1 for A" has text "DE" "candidate 4 for A list text (DE)"
    And position "my elections"."election A"."list 1 for A"."pos 3 list 1 for A" has text "FR" "candidate 4 for A list text (FR)"
    And position "my elections"."election A"."list 1 for A"."pos 3 list 1 for A" has text "IT" "candidate 4 for A list text (IT)"
    And position "my elections"."election A"."list 1 for A"."pos 3 list 1 for A" has text "RM" "candidate 4 for A list text (RM)"

    And position "my elections"."election A"."list 1 for A"."pos 4 list 1 for A" has id "candidate-3-for-A-id"
    And position "my elections"."election A"."list 1 for A"."pos 4 list 1 for A" has position in list 4
    And position "my elections"."election A"."list 1 for A"."pos 4 list 1 for A" has text "DE" "candidate 3 for A list text (DE)"
    And position "my elections"."election A"."list 1 for A"."pos 4 list 1 for A" has text "FR" "candidate 3 for A list text (FR)"
    And position "my elections"."election A"."list 1 for A"."pos 4 list 1 for A" has text "IT" "candidate 3 for A list text (IT)"
    And position "my elections"."election A"."list 1 for A"."pos 4 list 1 for A" has text "RM" "candidate 3 for A list text (RM)"

    And a position named "pos 1 list 2 for A" within "my elections"."election A"."list 2 for A"
    And a position named "pos 2 list 2 for A" within "my elections"."election A"."list 2 for A"

    And position "my elections"."election A"."list 2 for A"."pos 1 list 2 for A" has id "candidate-4-for-A-id"
    And position "my elections"."election A"."list 2 for A"."pos 1 list 2 for A" has position in list 1
    And position "my elections"."election A"."list 2 for A"."pos 1 list 2 for A" has text "DE" "candidate 4 for A list text (DE)"
    And position "my elections"."election A"."list 2 for A"."pos 1 list 2 for A" has text "FR" "candidate 4 for A list text (FR)"
    And position "my elections"."election A"."list 2 for A"."pos 1 list 2 for A" has text "IT" "candidate 4 for A list text (IT)"
    And position "my elections"."election A"."list 2 for A"."pos 1 list 2 for A" has text "RM" "candidate 4 for A list text (RM)"

    And position "my elections"."election A"."list 2 for A"."pos 2 list 2 for A" has id "candidate-3-for-A-id"
    And position "my elections"."election A"."list 2 for A"."pos 2 list 2 for A" has position in list 2
    And position "my elections"."election A"."list 2 for A"."pos 2 list 2 for A" has text "DE" "candidate 3 for A list text (DE)"
    And position "my elections"."election A"."list 2 for A"."pos 2 list 2 for A" has text "FR" "candidate 3 for A list text (FR)"
    And position "my elections"."election A"."list 2 for A"."pos 2 list 2 for A" has text "IT" "candidate 3 for A list text (IT)"
    And position "my elections"."election A"."list 2 for A"."pos 2 list 2 for A" has text "RM" "candidate 3 for A list text (RM)"

#  -------------------------------------------------------------------------------
#  Contest consistency checks
#  -------------------------------------------------------------------------------
  Scenario: The background given contest is consistent
    When executing the contest phase
    Then it outputs no exception
    And the contest xml is validated and displayed in the console logs
    And the contest default language is "DE"

  Scenario: Throw an exception when neither election nor vote is defined
    When elections "my elections" are cleared out
    And votes "my votes" are cleared out
    And elections in parameters "my params" are cleared out
    And executing the contest phase and catching exceptions
    Then it outputs an exception with text "neither election nor vote is defined"

  Scenario: Throw an exception when the eVoting date from is not future
    Given user parameters "my params" has eVoting date from "2001-01-01 00:00:00"
    When executing the contest phase and catching exceptions
    Then it outputs an exception with text "evotingFromDate must be in the future."

  Scenario: Throw an exception when the eVoting date to is not future
    Given user parameters "my params" has eVoting date to "2001-01-01 00:00:00"
    When executing the contest phase and catching exceptions
    Then it outputs an exception with text "evotingToDate must be in the future."

  Scenario: Throw an exception when eVoting date from precedes eVoting date to
    Given user parameters "my params" has eVoting date from "2045-07-13 00:00:00"
    And user parameters "my params" has eVoting date to "2045-07-11 00:00:00"
    When executing the contest phase and catching exceptions
    Then it outputs an exception with text "evotingFromDate must be before evotingToDate."

  Scenario: Throw an exception when the number of days between eVoting from and to is less than 10
    Given user parameters "my params" has eVoting date from "2045-05-11 13:00:00"
    And user parameters "my params" has eVoting date to "2045-05-20 09:00:00"
    When executing the contest phase and catching exceptions
    Then it outputs an exception with text "the number of days between 'eVoting date from' and 'eVoting date to' must be at least 10"

  Scenario: Warn when contest date does not follow eVoting date from
    Given  the contest "my contest" has date "2045-03-12 00:00:00"
    When executing the contest phase
    Then a log with text "The date of the contest should follow the eVoting date from." must exist

  Scenario: Throw an exception when parameters contains an election identifier not existing in the contest
    Given election "my params"."election 2" has id "---?---"
    When executing the contest phase and catching exceptions
    Then it outputs an exception with text "election identifier '---?---' defined in the parameter file doesn't exist in the contest"

  Scenario: Throw an exception when parameters contains duplicate answer types
    Given default answer type "my params"."answer type 2" has type 1
    When executing the contest phase and catching exceptions
    Then it outputs an exception with text "answerType '1' not unique within the parameters"

  Scenario: Throw an exception when parameters contains duplicate question ids
    Given parameter question "my params"."tiebreak question 2" has questionIdentification "V-B_B-1_TQ-1"
    When executing the contest phase and catching exceptions
    Then it outputs an exception with text "questionId 'V-B_B-1_TQ-1' not unique within the parameters"

  Scenario: Throw an exception when no answer exists for a question with a given type
    Given default answer type "my params"."answer type 1" has type 7
    When executing the contest phase and catching exceptions
    Then it outputs an exception with text "Process stopped. answerType 1 in question V-A_B-1_Q-0 is not supported"

  Scenario: Throw an exception when no answer exists for a question without type
    Given parameter question "my params"."tiebreak question 2" has questionIdentification "xyz"
    When executing the contest phase and catching exceptions
    Then it outputs an exception with text "Process stopped. answerType 4 in question V-B_B-1_TQ-2 is not supported"

  Scenario: Throw an exception when duplicated vote identifiers exist
    Given the vote "my votes"."vote B" has id "vote-A-id"
    When executing the contest phase and catching exceptions
    Then it outputs an exception with text "voteIdentification must be unique"

  Scenario: Throw an exception when duplicated election identifiers exist
    Given the election "my elections"."election B" has id "election-A-id"
    And the elections parameters in "my params" are cleared out
    When executing the contest phase and catching exceptions
    Then it outputs an exception with text "electionIdentification must be unique"

  Scenario: Throw an exception when duplicated ballot identifiers exist
    Given the ballot "my votes"."vote B"."vote B ballot 1" has id "vote-A-ballot-1-id"
    When executing the contest phase and catching exceptions
    Then it outputs an exception with text "ballot identifier 'vote-A-ballot-1-id' is not unique within the contest"

  Scenario: Throw an exception when duplicated question identifiers exist
    Given the tiebreak question "my votes"."vote B"."vote B ballot 1"."vote B ballot 1 tb question 2" has id "V-B_B-1_TQ-1"
    When executing the contest phase and catching exceptions
    Then it outputs an exception with text "question identifier 'V-B_B-1_TQ-1' is not unique within the contest"

  Scenario: Throw an exception when duplicated answer identifiers exist, setting duplicate specific answer position
    Given tiebreakAnswer "my params"."tiebreak question 1"."answer 2" has answer position 1
    When executing the contest phase and catching exceptions
    Then it outputs an exception with text "answer identifier 'df57dfcb-7934-37a8-a3fc-0f40630e1432' is not unique within the contest"

  Scenario: Throw an exception when duplicated answer identifiers exist, setting duplicate default answer position
    Given standardAnswer "my params"."answer type 2"."answer 2" has answer position 3
    When executing the contest phase and catching exceptions
    Then it outputs an exception with text "answer identifier '0a851e9f-1d04-3adc-9ea0-455015e78e95' is not unique within the contest"

  Scenario: For variant ballot, all the standard questions positions are given (background scenario)
    When executing the contest phase
    Then the variant ballot standard question "V-B_B-1_Q-proposal_A" should have position 3
    And the variant ballot standard question "V-B_B-1_Q-proposal_B" should have position 2
    And the variant ballot standard question "V-B_B-1_Q-proposal_C" should have position 1

  Scenario: For a variant ballot, all the standard questions positions are missing
    Given the standard question "my votes"."vote B"."vote B ballot 1"."vote B ballot 1 question 1" has position cleared out
    And the standard question "my votes"."vote B"."vote B ballot 1"."vote B ballot 1 question 2" has position cleared out
    And the standard question "my votes"."vote B"."vote B ballot 1"."vote B ballot 1 question 3" has position cleared out
    When executing the contest phase
    Then the variant ballot standard question "V-B_B-1_Q-proposal_A" should have position 1
    And the variant ballot standard question "V-B_B-1_Q-proposal_B" should have position 2
    And the variant ballot standard question "V-B_B-1_Q-proposal_C" should have position 3

  Scenario: Throw an exception when, for a variant ballot, one of the standard questions position is missing
    Given the standard question "my votes"."vote B"."vote B ballot 1"."vote B ballot 1 question 2" has position cleared out
    When executing the contest phase and catching exceptions
    Then it outputs an exception with text "Variant ballot 'vote-B-ballot-1-id' : missing position for some questions; either all questions must have a position or none"

  Scenario: For a variant ballot, all the tiebreak questions positions are given (background scenario)
    When executing the contest phase
    Then the variant ballot tiebreak question "V-B_B-1_TQ-1" should have position 4
    And the variant ballot tiebreak question "V-B_B-1_TQ-2" should have position 6
    And the variant ballot tiebreak question "V-B_B-1_TQ-3" should have position 5

  Scenario: Throw an exception when, for a variant ballot, all the tiebreak questions positions are missing
    And the tiebreak question "my votes"."vote B"."vote B ballot 1"."vote B ballot 1 tb question 1" has position cleared out
    And the tiebreak question "my votes"."vote B"."vote B ballot 1"."vote B ballot 1 tb question 2" has position cleared out
    And the tiebreak question "my votes"."vote B"."vote B ballot 1"."vote B ballot 1 tb question 3" has position cleared out
    When executing the contest phase and catching exceptions
    Then it outputs an exception with text "Variant ballot 'vote-B-ballot-1-id' : questions positions must be a strictly increasing sequence from 1 to 6"

  Scenario: Throw an exception when, for a variant ballot, one of the tiebreak questions position is missing
    And the tiebreak question "my votes"."vote B"."vote B ballot 1"."vote B ballot 1 tb question 2" has position cleared out
    When executing the contest phase and catching exceptions
    Then it outputs an exception with text "Variant ballot 'vote-B-ballot-1-id' : questions positions must be a strictly increasing sequence from 1 to 6"

  Scenario: Throw an exception when some contest descriptions languages are missing
    Given the descriptions of the contest "my contest" are cleared out
    And the contest "my contest" has description "DE" "Test contest (DE)"
    And the contest "my contest" has description "FR" "Test contest (FR)"
    When executing the contest phase and catching exceptions
    Then it outputs an exception with text "contestDescription must contain at least 4 distinct languages"

  Scenario: Throw an exception when some contest descriptions languages are duplicated
    Given the descriptions of the contest "my contest" are cleared out
    And the contest "my contest" has description "DE" "Test contest (DE)"
    And the contest "my contest" has description "FR" "Test contest (FR)"
    And the contest "my contest" has description "IT" "Test contest (IT) 1"
    And the contest "my contest" has description "IT" "Test contest (IT) 2"
    When executing the contest phase and catching exceptions
    Then it outputs an exception with text "contestDescription must contain at least 4 distinct languages"

  Scenario: Throw an exception when some vote descriptions languages are missing
    Given the descriptions of the vote "my votes"."vote A" are cleared out
    And the vote "my votes"."vote A" has description "IT" "Test vote A (IT)"
    When executing the contest phase and catching exceptions
    Then it outputs an exception with text "vote 'vote-A-id' : voteDescription must contain at least 4 distinct languages"

  Scenario: Throw an exception when some vote descriptions languages are duplicated
    Given the descriptions of the vote "my votes"."vote A" are cleared out
    And the vote "my votes"."vote A" has description "IT" "Test vote A (IT) 1"
    And the vote "my votes"."vote A" has description "IT" "Test vote A (IT) 2"
    When executing the contest phase and catching exceptions
    Then it outputs an exception with text "vote 'vote-A-id' : voteDescription must contain at least 4 distinct languages"

  Scenario: Throw an exception when some ballot descriptions languages are missing
    Given the descriptions of the ballot "my votes"."vote B"."vote B ballot 1" are cleared out
    When executing the contest phase and catching exceptions
    Then it outputs an exception with text "ballot 'vote-B-ballot-1-id' : ballotDescription must contain at least 4 distinct languages"

  Scenario: Throw an exception when some ballot descriptions languages are duplicated
    Given the descriptions of the ballot "my votes"."vote B"."vote B ballot 1" are cleared out
    And the ballot "my votes"."vote B"."vote B ballot 1" has description "RM" "vote B ballot 1 description (RM)"
    And the ballot "my votes"."vote B"."vote B ballot 1" has description "FR" "vote B ballot 1 description (FR) 1"
    And the ballot "my votes"."vote B"."vote B ballot 1" has description "FR" "vote B ballot 1 description (FR) 2"
    When executing the contest phase and catching exceptions
    Then it outputs an exception with text "ballot 'vote-B-ballot-1-id' : ballotDescription must contain at least 4 distinct languages"

  Scenario: Throw an exception when some standard ballot question information languages are missing
    Given the descriptions of the ballot question of ballot "my votes"."vote A"."vote A ballot 1" are cleared out
    And the ballot question of ballot "my votes"."vote A"."vote A ballot 1" has info "FR" "Ballot question title (FR)" "Do you accept this proposal (FR)"
    When executing the contest phase and catching exceptions
    Then it outputs an exception with text "question 'V-A_B-1_Q-0' : ballotQuestion must contain at least 4 distinct languages"

  Scenario: Throw an exception when some standard ballot question information languages are duplicated
    Given the descriptions of the ballot question of ballot "my votes"."vote A"."vote A ballot 1" are cleared out
    And the ballot question of ballot "my votes"."vote A"."vote A ballot 1" has info "DE" "Proposal" "Do you accept this proposal (DE)"
    And the ballot question of ballot "my votes"."vote A"."vote A ballot 1" has info "RM" "Proposal" "Do you accept this proposal (RM)"
    And the ballot question of ballot "my votes"."vote A"."vote A ballot 1" has info "FR" "Proposal" "Do you accept this proposal (FR) 1"
    And the ballot question of ballot "my votes"."vote A"."vote A ballot 1" has info "FR" "Proposal" "Do you accept this proposal (FR) 2"
    When executing the contest phase and catching exceptions
    Then it outputs an exception with text "question 'V-A_B-1_Q-0' : ballotQuestion must contain at least 4 distinct languages"

  Scenario: Throw an exception when some variant ballot question information languages are missing
    Given the descriptions of the standard question  "my votes"."vote B"."vote B ballot 1"."vote B ballot 1 question 1" are cleared out
    And the standard question "my votes"."vote B"."vote B ballot 1"."vote B ballot 1 question 1" has info "DE" "Question 1 Title" "Question 1 info (DE)"
    And the standard question "my votes"."vote B"."vote B ballot 1"."vote B ballot 1 question 1" has info "RM" "Question 1 Title" "Question 1 info (RM)"
    When executing the contest phase and catching exceptions
    Then it outputs an exception with text "question 'V-B_B-1_Q-proposal_A' : ballotQuestion must contain at least 4 distinct languages"

  Scenario: Throw an exception when some variant ballot question information languages are duplicated
    Given the descriptions of the standard question  "my votes"."vote B"."vote B ballot 1"."vote B ballot 1 question 1" are cleared out
    And the standard question "my votes"."vote B"."vote B ballot 1"."vote B ballot 1 question 1" has info "DE" "Question 1 Title" "Question 1 info (DE)"
    And the standard question "my votes"."vote B"."vote B ballot 1"."vote B ballot 1 question 1" has info "RM" "Question 1 Title" "Question 1 info (RM) 1"
    And the standard question "my votes"."vote B"."vote B ballot 1"."vote B ballot 1 question 1" has info "RM" "Question 1 Title" "Question 1 info (RM) 2"
    And the standard question "my votes"."vote B"."vote B ballot 1"."vote B ballot 1 question 1" has info "IT" "Question 1 Title" "Question 1 info (IT)"
    When executing the contest phase and catching exceptions
    Then it outputs an exception with text "question 'V-B_B-1_Q-proposal_A' : ballotQuestion must contain at least 4 distinct languages"

  Scenario: Throw an exception when some variant ballot tiebreak information languages are missing
    Given the descriptions of the tiebreak question "my votes"."vote B"."vote B ballot 1"."vote B ballot 1 tb question 2" are cleared out
    And the tiebreak question "my votes"."vote B"."vote B ballot 1"."vote B ballot 1 tb question 2" has info "FR" "Tie-break question AC" "Do you prefer A or C (FR)"
    When executing the contest phase and catching exceptions
    Then it outputs an exception with text "question 'V-B_B-1_TQ-2' : ballotQuestion must contain at least 4 distinct languages"

  Scenario: Throw an exception when some variant ballot tiebreak information languages are duplicated
    Given the descriptions of the tiebreak question "my votes"."vote B"."vote B ballot 1"."vote B ballot 1 tb question 2" are cleared out
    And the tiebreak question "my votes"."vote B"."vote B ballot 1"."vote B ballot 1 tb question 2" has info "IT" "Tie-break question AC" "Do you prefer A or C (IT)"
    And the tiebreak question "my votes"."vote B"."vote B ballot 1"."vote B ballot 1 tb question 2" has info "FR" "Tie-break question AC" "Do you prefer A or C (FR) 1"
    And the tiebreak question "my votes"."vote B"."vote B ballot 1"."vote B ballot 1 tb question 2" has info "FR" "Tie-break question AC" "Do you prefer A or C (FR) 2"
    And the tiebreak question "my votes"."vote B"."vote B ballot 1"."vote B ballot 1 tb question 2" has info "FR" "Tie-break question AC" "Do you prefer A or C (FR) 3"
    When executing the contest phase and catching exceptions
    Then it outputs an exception with text "question 'V-B_B-1_TQ-2' : ballotQuestion must contain at least 4 distinct languages"

  Scenario: Throw an exception when some default answers descriptions languages are missing
    Given the descriptions of the answer "my params"."answer type 2"."answer 2" are cleared out
    And standardAnswer "my params"."answer type 2"."answer 2" has answer description "DE" "Ja"
    When executing the contest phase and catching exceptions
    Then it outputs an exception with text "answer '463caf46-4f2b-3893-a956-c7b21feca3d1' : answer must contain at least 4 distinct languages"

  Scenario: Throw an exception when some default answers descriptions languages are duplicated
    Given the descriptions of the answer "my params"."answer type 2"."answer 2" are cleared out
    And standardAnswer "my params"."answer type 2"."answer 2" has answer description "DE" "Ja 1"
    And standardAnswer "my params"."answer type 2"."answer 2" has answer description "DE" "Ja 2"
    And standardAnswer "my params"."answer type 2"."answer 2" has answer description "FR" "Oui"
    And standardAnswer "my params"."answer type 2"."answer 2" has answer description "IT" "Si"
    When executing the contest phase and catching exceptions
    Then it outputs an exception with text "answer '463caf46-4f2b-3893-a956-c7b21feca3d1' : answer must contain at least 4 distinct languages"

  Scenario: Throw an exception when some specific answers descriptions languages are missing
    Given the descriptions of the answer "my params"."question variant C"."answer 2" are cleared out
    And standardAnswer "my params"."question variant C"."answer 2" has answer description "DE" "Nein Nein"
    When executing the contest phase and catching exceptions
    Then it outputs an exception with text "answer '20edcbea-8bd1-32b0-9e82-ea624ed4beb5' : answer must contain at least 4 distinct languages"

  Scenario: Throw an exception when some specific answers descriptions languages are duplicated
    Given the descriptions of the answer "my params"."question variant C"."answer 2" are cleared out
    And standardAnswer "my params"."question variant C"."answer 2" has answer description "DE" "Nein Nein"
    And standardAnswer "my params"."question variant C"."answer 2" has answer description "FR" "Oui Oui 1"
    And standardAnswer "my params"."question variant C"."answer 2" has answer description "FR" "Oui Oui 2"
    And standardAnswer "my params"."question variant C"."answer 2" has answer description "IT" "Si Si"
    When executing the contest phase and catching exceptions
    Then it outputs an exception with text "answer '20edcbea-8bd1-32b0-9e82-ea624ed4beb5' : answer must contain at least 4 distinct languages"

  Scenario: Throw an exception when some tiebreak answers descriptions languages are missing
    Given the descriptions of the answer "my params"."tiebreak question 3"."answer 1" are cleared out
    And tiebreakAnswer "my params"."tiebreak question 3"."answer 1" has answer description "RM" "I prefer B (RM)"
    And tiebreakAnswer "my params"."tiebreak question 3"."answer 1" has answer description "IT" "I prefer B (IT)"
    When executing the contest phase and catching exceptions
    Then it outputs an exception with text "answer '2a0f687a-83cb-3ceb-98eb-5a5c27d07361' : answer must contain at least 4 distinct languages"

  Scenario:Throw an exception when some tiebreak answers descriptions languages are duplicated
    Given the descriptions of the answer "my params"."tiebreak question 3"."answer 1" are cleared out
    And tiebreakAnswer "my params"."tiebreak question 3"."answer 1" has answer description "RM" "I prefer B (RM)"
    And tiebreakAnswer "my params"."tiebreak question 3"."answer 1" has answer description "IT" "I prefer B (IT) 1"
    And tiebreakAnswer "my params"."tiebreak question 3"."answer 1" has answer description "IT" "I prefer B (IT) 2"
    And tiebreakAnswer "my params"."tiebreak question 3"."answer 1" has answer description "FR" "I prefer B (FR)"
    When executing the contest phase and catching exceptions
    Then it outputs an exception with text "answer '2a0f687a-83cb-3ceb-98eb-5a5c27d07361' : answer must contain at least 4 distinct languages"


  Scenario: Throw an exception when some election descriptions languages are missing
    Given the descriptions of the election "my elections"."election A" are cleared out
    And the election "my elections"."election A" has description "DE" "Test election A (DE)"
    And the election "my elections"."election A" has description "FR" "Test election A (FR)"
    When executing the contest phase and catching exceptions
    Then it outputs an exception with text "election 'election-A-id' : electionDescription must contain at least 4 distinct languages"

  Scenario: Throw an exception when some election descriptions languages are duplicated
    Given the descriptions of the election "my elections"."election B" are cleared out
    And the election "my elections"."election B" has description "DE" "Test election B (DE)"
    And the election "my elections"."election B" has description "FR" "Test election B (FR)"
    And the election "my elections"."election B" has description "RM" "Test election B (RM) 1"
    And the election "my elections"."election B" has description "RM" "Test election B (RM) 2"
    When executing the contest phase and catching exceptions
    Then it outputs an exception with text "election 'election-B-id' : electionDescription must contain at least 4 distinct languages"

  Scenario: Throw an exception when some candidate descriptions languages are missing
    Given the text descriptions of the candidate "my elections"."election A"."candidate 1 for A" are cleared out
    And candidate "my elections"."election A"."candidate 1 for A" has text "FR" "Candidate 1 for A text (FR)"
    And candidate "my elections"."election A"."candidate 1 for A" has text "IT" "Candidate 1 for A text (IT)"
    When executing the contest phase and catching exceptions
    Then it outputs an exception with text "candidate 'candidate-1-for-A-id' : candidateText must contain at least 4 distinct languages"

  Scenario: Throw an exception when some candidate descriptions languages are duplicated
    Given the text descriptions of the candidate "my elections"."election A"."candidate 2 for A" are cleared out
    And candidate "my elections"."election A"."candidate 2 for A" has text "FR" "Candidate 2 for A text (FR)"
    And candidate "my elections"."election A"."candidate 2 for A" has text "IT" "Candidate 2 for A text (IT) 1"
    And candidate "my elections"."election A"."candidate 2 for A" has text "IT" "Candidate 2 for A text (IT) 2"
    And candidate "my elections"."election A"."candidate 2 for A" has text "RM" "Candidate 2 for A text (RM)"
    When executing the contest phase and catching exceptions
    Then it outputs an exception with text "candidate 'candidate-2-for-A-id' : candidateText must contain at least 4 distinct languages"

  Scenario: Throw an exception when some but not all candidate texts are missing
    Given the text descriptions of the candidate "my elections"."election A"."candidate 1 for A" are cleared out
    When executing the contest phase and catching exceptions
    Then it outputs an exception with text "The field candidateText for one or more candidates was not provided: [electionIdentification: election-A-id, candidateId: candidate-1-for-A-id]"

  Scenario: Log a warning when all candidate texts are missing
    Given the text descriptions of the candidate "my elections"."election A"."candidate 1 for A" are cleared out
    And the text descriptions of the candidate "my elections"."election A"."candidate 2 for A" are cleared out
    And the text descriptions of the candidate "my elections"."election A"."candidate 3 for A" are cleared out
    And the text descriptions of the candidate "my elections"."election A"."candidate 4 for A" are cleared out
    And the text descriptions of the candidate "my elections"."election A"."candidate 5 for A" are cleared out
    When executing the contest phase
    Then a log with text "The candidateText in the election were not provided and dummy texts were assigned automatically. [electionIdentification: election-A-id]" must exist

  Scenario: Throw an exception when some candidates occupational title descriptions languages are missing
    Given the title descriptions of the candidate "my elections"."election A"."candidate 1 for A" are cleared out
    And candidate "my elections"."election A"."candidate 1 for A" has occupational title "FR" "Candidate 1 for A title (FR)"
    When executing the contest phase and catching exceptions
    Then it outputs an exception with text "candidate 'candidate-1-for-A-id' : occupationalTitle must contain at least 4 distinct languages"

  Scenario: Throw an exception when some candidates occupational title descriptions languages are duplicated
    Given the title descriptions of the candidate "my elections"."election A"."candidate 2 for A" are cleared out
    And candidate "my elections"."election A"."candidate 2 for A" has occupational title "DE" "Candidate 2 for A title (DE)"
    And candidate "my elections"."election A"."candidate 2 for A" has occupational title "FR" "Candidate 2 for A title (FR) 1"
    And candidate "my elections"."election A"."candidate 2 for A" has occupational title "FR" "Candidate 2 for A title (FR) 2"
    And candidate "my elections"."election A"."candidate 2 for A" has occupational title "IT" "Candidate 2 for A title (IT)"
    When executing the contest phase and catching exceptions
    Then it outputs an exception with text "candidate 'candidate-2-for-A-id' : occupationalTitle must contain at least 4 distinct languages"

  Scenario: Throw an exception when some list descriptions languages are missing
    Given the list descriptions of the list "my elections"."election A"."list 2 for A" are cleared out
    And list "my elections"."election A"."list 2 for A" has description "IT" "list 2 for election A (IT)"
    And list "my elections"."election A"."list 2 for A" has description "RM" "list 2 for election A (RM)"
    When executing the contest phase and catching exceptions
    Then it outputs an exception with text "list 'list-2-for-election-A-id' : listDescription must contain at least 4 distinct languages"

  Scenario: Throw an exception when some list descriptions languages are duplicated
    Given the list descriptions of the list "my elections"."election A"."list 1 for A" are cleared out
    And list "my elections"."election A"."list 1 for A" has description "IT" "list 1 for election A (IT) 1"
    And list "my elections"."election A"."list 1 for A" has description "IT" "list 1 for election A (IT) 2"
    And list "my elections"."election A"."list 1 for A" has description "IT" "list 1 for election A (IT) 3"
    And list "my elections"."election A"."list 1 for A" has description "RM" "list 1 for election A (RM)"
    When executing the contest phase and catching exceptions
    Then it outputs an exception with text "list 'list-1-for-election-A-id' : listDescription must contain at least 4 distinct languages"

  Scenario: Throw an exception when a candidate identification in election is not unique
    Given candidate "my elections"."election A"."candidate 4 for A" has id "candidate-3-for-A-id"
    When executing the contest phase and catching exceptions
    Then it outputs an exception with text "election 'election-A-id' : candidateIdentification must be unique"

  Scenario: Throw an exception when position in a list is not unique
    Given position "my elections"."election A"."list 2 for A"."pos 2 list 2 for A" has position in list 1
    When executing the contest phase and catching exceptions
    Then it outputs an exception with text "the position 1 in the list 'list-2-for-election-A-id' is not unique"

  Scenario: Throw an exception when a candidate identification in a list is not found in the list of candidate(s)
    Given position "my elections"."election A"."list 1 for A"."pos 4 list 1 for A" has id "wrong-id"
    When executing the contest phase and catching exceptions
    Then it outputs an exception with text "candidate identifier 'wrong-id' found in a list but not in the list of candidate(s)"

  Scenario: Throw an exception when some candidate text descriptions languages are missing
    Given the candidate text descriptions at position "my elections"."election A"."list 1 for A"."pos 1 list 1 for A" are cleared out
    And position "my elections"."election A"."list 1 for A"."pos 1 list 1 for A" has text "FR" "candidate 2 for A list text (FR)"
    And position "my elections"."election A"."list 1 for A"."pos 1 list 1 for A" has text "IT" "candidate 2 for A list text (IT)"
    When executing the contest phase and catching exceptions
    Then it outputs an exception with text "candidate 'candidate-2-for-A-id' : candidateTextOnPosition must contain at least 4 distinct languages"

  Scenario: Throw an exception when some candidate text descriptions languages are duplicated
    Given the candidate text descriptions at position "my elections"."election A"."list 1 for A"."pos 1 list 1 for A" are cleared out
    And position "my elections"."election A"."list 1 for A"."pos 1 list 1 for A" has text "DE" "candidate 2 for A list text (DE)"
    And position "my elections"."election A"."list 1 for A"."pos 1 list 1 for A" has text "FR" "candidate 2 for A list text (FR)"
    And position "my elections"."election A"."list 1 for A"."pos 1 list 1 for A" has text "IT" "candidate 2 for A list text (IT) 1"
    And position "my elections"."election A"."list 1 for A"."pos 1 list 1 for A" has text "IT" "candidate 2 for A list text (IT) 2"
    When executing the contest phase and catching exceptions
    Then it outputs an exception with text "candidate 'candidate-2-for-A-id' : candidateTextOnPosition must contain at least 4 distinct languages"

  Scenario: No writeIns in parameter will produce elections with default values (=false)
    Given the elections parameters in "my params" are cleared out
    When executing the contest phase
    Then all contest elections should have writeIns = false

  Scenario: writeIns configured in parameter will produce elections with corresponding values
    Given the elections parameters in "my params" are cleared out
    And user parameters "my params" has election named "election 1"
    And election "my params"."election 1" has id "election-A-id"
    And election "my params"."election 1" has writeIns "false"
    And user parameters "my params" has election named "election 2"
    And election "my params"."election 2" has id "election-B-id"
    When executing the contest phase
    Then election identified by "election-A-id" should have writeIns "false"
    And election identified by "election-B-id" should have writeIns "false"

  Scenario: minimalCandidateSelectionInList configured from parameter
    Given the elections parameters in "my params" are cleared out
    And user parameters "my params" has election named "election 1"
    And election "my params"."election 1" has id "election-A-id"
    And election "my params"."election 1" has minimalCandidateSelection 0
    And user parameters "my params" has election named "election 2"
    And election "my params"."election 2" has id "election-B-id"
    When executing the contest phase
    Then election identified by "election-A-id" should have minimalCandidateSelection 0
    And election identified by "election-B-id" should have minimalCandidateSelection 1

  Scenario: Throw an exception when the number of accumulated candidates in list exceed the accumulation parameter value (=1) of the election
    Given election "my params"."election 1" has accumulation 1
    And position "my elections"."election A"."list 1 for A"."pos 2 list 1 for A" has id "candidate-2-for-A-id"
    When executing the contest phase and catching exceptions
    Then it outputs an exception with text "same candidate found more than 1 time(s). [listIdentification : list-1-for-election-A-id]"

  Scenario: Throw an exception when the number of accumulated candidates in list exceed the accumulation parameter value (=3) of the election
    Given election "my params"."election 1" has accumulation 3
    And position "my elections"."election A"."list 1 for A"."pos 1 list 1 for A" has id "candidate-4-for-A-id"
    And position "my elections"."election A"."list 1 for A"."pos 2 list 1 for A" has id "candidate-4-for-A-id"
    And position "my elections"."election A"."list 1 for A"."pos 3 list 1 for A" has id "candidate-4-for-A-id"
    And position "my elections"."election A"."list 1 for A"."pos 4 list 1 for A" has id "candidate-4-for-A-id"
    When executing the contest phase and catching exceptions
    Then it outputs an exception with text "same candidate found more than 3 time(s). [listIdentification : list-1-for-election-A-id]"

  Scenario: An accumulated candidate in a list must have the same candidateListIdentification
    Given election "my params"."election 1" has accumulation 3
    And position "my elections"."election A"."list 1 for A"."pos 1 list 1 for A" has id "candidate-1-for-A-id"
    And position "my elections"."election A"."list 1 for A"."pos 2 list 1 for A" has id "candidate-2-for-A-id"
    And position "my elections"."election A"."list 1 for A"."pos 3 list 1 for A" has id "candidate-2-for-A-id"
    And position "my elections"."election A"."list 1 for A"."pos 4 list 1 for A" has id "candidate-2-for-A-id"
    When executing the contest phase
    Then the list identified by "list-1-for-election-A-id" should contain 3 occurrences of the candidateListIdentification "f532dee3-1088-33d0-b471-8f7a257a80bd"

  Scenario: Throw an exception when a list-union identifier is not unique
    Given list-union  "my elections"."election A"."list-union 2" has id "list-union-1-id"
    When executing the contest phase and catching exceptions
    Then it outputs an exception with text "the list-union identifier 'list-union-1-id' is not unique"

  Scenario: Throw an exception when a referenced list is found multiple times in a list-union
    Given list-union  "my elections"."election A"."list-union 2" has referenced list "list-1-for-election-A-id"
    When executing the contest phase and catching exceptions
    Then it outputs an exception with text "the referenced list 'list-1-for-election-A-id' in the list-union 'list-union-2-id' is not unique"

  Scenario: Throw an exception when a referenced list in list-union does not exist
    Given list-union  "my elections"."election A"."list-union 2" has referenced list "--?--"
    When executing the contest phase and catching exceptions
    Then it outputs an exception with text "referenced list identifier '--?--' found in a list-union does not exist"

  Scenario: Throw an exception when some list-union descriptions languages are missing
    Given the list-union descriptions for "my elections"."election A"."list-union 2" are cleared out
    And list-union  "my elections"."election A"."list-union 2" has description "DE" "list-union-2 text (DE)"
    When executing the contest phase and catching exceptions
    Then it outputs an exception with text "list-union 'list-union-2-id' : listUnionDescription must contain at least 4 distinct languages"

  Scenario: Throw an exception when some list-union descriptions languages are duplicated
    Given the list-union descriptions for "my elections"."election A"."list-union 2" are cleared out
    And list-union  "my elections"."election A"."list-union 2" has description "DE" "list-union-2 text (DE)"
    And list-union  "my elections"."election A"."list-union 2" has description "FR" "list-union-2 text (FR) 1"
    And list-union  "my elections"."election A"."list-union 2" has description "FR" "list-union-2 text (FR) 2"
    And list-union  "my elections"."election A"."list-union 2" has description "RM" "list-union-2 text (RM)"
    When executing the contest phase and catching exceptions
    Then it outputs an exception with text "list-union 'list-union-2-id' : listUnionDescription must contain at least 4 distinct languages"

  Scenario: Warn if a candidate name is found multiple times in an election
    Given candidate "my elections"."election A"."candidate 3 for A" has name "first-name-4" "last-name-4"
    When executing the contest phase
    Then a log with text "same candidate 'first-name-4' 'last-name-4' name found multiple times. [ElectionId : election-A-id]" must exist

  Scenario: Throw an exception when some but not all candidate's referenceOnPosition is missing
      Given the referenceOnPosition of the candidate "my elections"."election A"."candidate 1 for A" is cleared out
      When executing the contest phase and catching exceptions
      Then it outputs an exception with text "The field candidateReference for one or more candidates was not provided: [electionIdentification: election-A-id, candidateId: candidate-1-for-A-id]"

  Scenario: Warn if all candidates have their referenceOnPosition missing
    Given the referenceOnPosition of the candidate "my elections"."election A"."candidate 1 for A" is cleared out
    Given the referenceOnPosition of the candidate "my elections"."election A"."candidate 2 for A" is cleared out
    Given the referenceOnPosition of the candidate "my elections"."election A"."candidate 3 for A" is cleared out
    Given the referenceOnPosition of the candidate "my elections"."election A"."candidate 4 for A" is cleared out
    Given the referenceOnPosition of the candidate "my elections"."election A"."candidate 5 for A" is cleared out
    When executing the contest phase
    Then a log with text "The candidateReference in the election were not provided and were assigned automatically. [electionIdentification: election-A-id]" must exist

  Scenario: Merging all contest formats
    When executing the contest phase with parameters "my params" and contest "my contest" and votes "my votes" and elections "my elections"
    Then it outputs a contest with id "xxxx-yyyy"


  Scenario: ListOrderOfPrecedence field on lists are filled by sorting by ListIndentureNumber if at least one value is empty
    Given list "my elections"."election A"."list 1 for A" has no listOrderOfPrecedence
    Given list "my elections"."election A"."list 3 for A" has no listOrderOfPrecedence
    When executing the contest phase
    Then the list identified by "list-1-for-election-A-id" should have listOrderOfPrecedence 3
    And the list identified by "list-2-for-election-A-id" should have listOrderOfPrecedence 2
    And the list identified by "list-3-for-election-A-id" should have listOrderOfPrecedence 1

  Scenario: Throw an exception when a standard ballot question has two blank responses
    Given standardAnswer "my params"."answer type 2"."answer 2" has answer standardAnswerType = "EMPTY"
    When executing the contest phase and catching exceptions
    Then it outputs an exception with text "vote 'vote-B-id' variant ballot standard question 3 has more than one blank answer"

  Scenario: Throw an exception when variant ballot standard question has two blank responses
    Given standardAnswer "my params"."question variant C"."answer 1" has answer standardAnswerType = "EMPTY"
    Given standardAnswer "my params"."question variant C"."answer 2" has answer standardAnswerType = "EMPTY"
    When executing the contest phase and catching exceptions
    Then it outputs an exception with text "vote 'vote-B-id' variant ballot standard question 1 has more than one blank answer"

  Scenario: Throw an exception when variant ballot tiebreak question has two blank responses
    Given tiebreakAnswer "my params"."tiebreak question 3"."answer 1" is as blank defined
    And tiebreakAnswer "my params"."tiebreak question 3"."answer 2" is as blank defined
    When executing the contest phase and catching exceptions
    Then it outputs an exception with text "vote 'vote-B-id' variant ballot tiebreak question 5 has more than one blank answer"
