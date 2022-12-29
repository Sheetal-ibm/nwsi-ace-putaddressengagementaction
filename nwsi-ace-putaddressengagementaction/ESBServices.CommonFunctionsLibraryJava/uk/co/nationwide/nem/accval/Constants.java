package uk.co.nationwide.nem.accval;

class Constants {

	public final static String MORTGAGE = "M"; // ICL mortgage

	public final static String INVESTMENT = "I"; // Unisys or ICL investment
	public final static String CASHBUILDER = "I"; // cash builder and invest
													// direct accounts -
													// reported as investments
	public final static String FLEXACCOUNT = "F"; // Flexaccount
	public final static String CREDITCARD = "C"; // Classic or Gold credit cards
	public final static String SUSPENSE = "S"; // Suspense account
	public final static String PERSONALOAN = "P"; // Personal loan
	public final static String CREDINSURN = "N"; // Creditor insurance
	public final static String RBSINVEST = "R"; // RBS investment
	public final static String TRUSTLOAN = "T"; // Nationwide Trust loan
	public final static String EQUITY = "E"; // Mellon client number
	public final static String UCB = "U"; // UCB mortgage number
	public final static String OTHER = "O"; // 13 digit VISA or not recognised
	public final static String BOND = "J"; // Bond account
	public final static String BUSINVEST = "B"; // Business investor accs.
	public final static String PORTINVEST = "B"; // Portfolio investor accs
													// (business and portfolio
													// investor accs
	// not differentiated at present.
	public final static String MPOS = "Q"; // MPOS number
	public final static String PROHOME = "H"; // progen Home insurance policy
	public final static String PROPAY = "K"; // progen Payment Protection Policy
	public final static String PROOTHER = "L"; // progen insurance number
												// unknown type
	public final static String PERSLOANAIP = "A"; // personalLoan Approval in
													// Principal
	public final static String MELLON = "D"; // Mellon account number
	public final static String CAPSIL = "G"; // Capsil account number
	public final static String IPOS = "V"; // IPOS number
	public final static String PORTMAN = "W"; // Portman number
	public final static String LANDG = "X"; // L&G insurance no.
	public final static String NEWFLEX = "NF"; // internal use only.
	public final static String NEWCARD = "NC"; // internal use only.
	public final static String NIRVANA = "Y"; // lv policies.
	public final static String STAKEHOLDERPENSION = "Z"; // Mellon and L&G
															// stakeholder
															// pension
	public final static String CONIFERSAVINGS = "1"; // Conifer third party
														// savings account
	public final static String MSO = "2"; // mso mortgage application number
	public final static String NEWMAN = "3"; // Newman lifestyle policy (MSO is
												// 2, but not implemented yet)
	public final static String RBSIGENINS = "4"; // RBSI General Insurance
	public final static String REGBRAND = "5"; // regional brand number

	public final static String TRAVELINS = "7"; // Current Account Travel Insurance number - Added as part of SAMM TFR Def#1046
	
	public final static String COOP_SCAN = "086086";

	public final static String ClassicIsoCode = "449352"; // Classic Nationwide
															// Credit Card
	public final static String GoldIsoCode = "449353"; // Gold Nationwide Credit
														// Card
	public final static String StandardIsoCode = "489394"; // Standard
															// Nationwide Credit
															// Card
	public final static String AvaIsoCode = "489395"; // AVA Nationwide Credit
														// Card
	public final static String ChargecardIsoCode = "489396"; // Chargecard
																// Nationwide
																// Credit Card
	// Select World Mastercard Nationwide Credit Card - removed as non-visa
	// cards were given up on.
	public final static String InvestIsoCode = "564120"; // includes the
															// following 4 card
															// types
	public final static String CashBuilderIsoCode = "445803"; // visa plus iso
																// code
	public final static String InvestDirectIsoCode = "445804"; // visa plus iso
																// code
	public final static String SmartIsoCode = "459000"; // visa plus iso code
	public final static String RegularSavingsIsoCode = "459001"; // visa plus
																	// iso code
	public final static String VisaIsoCode = "454313";
	public final static String GoldDebitVisaIsoCode = "456729";
	public final static String FlexCGCIsoCode = "564192";
	public final static String FlexATMIsoCode = "639000";
	public final static String CashCardIsoCode = "465935"; // new pan for
															// FlexATM issued by
															// Visa plus

	// Voyager Account BINs
	public final static String VoyagerCardIsoCode1 = "475139";
	public final static String VoyagerCardIsoCode2 = "475140";
	public final static String VoyagerCardIsoCode3 = "475141";
	// iso code 4 removed - morphed into flex one x2x
	// public final static String VoyagerCardIsoCode4 = "475142";
	public final static String VoyagerCardIsoCode5 = "475143";
	public final static String VoyagerCardIsoCode6 = "475144";
	public final static String VoyagerCardIsoCode7 = "475145";
	
	// WP1129 - SO SAP Savings Product
	public final static String SOSAPSavingsIsoCode = "459478";
	
	//WP1518 - BCA Flex Product
	public final static String BusinessDebitVisaCard = "456742";
		
	// Nationwide Sort Code range
	public final static String MinSortCode = "070000";
	public final static String MaxSortCode = "074999";

	// Savings Limited Access bins
	public final static String SavingsOneAdultIsoCode = "459475";
	public final static String SavingsOneChildIsoCode = "459476";

	// Regional Brand ATM bin
	public final static String RegionalBrandATMIsoCode = "459484";

	// Flex one bins
	public final static String FlexOneCashCardIsoCode = "459480";
	public final static String FlexOnex2xIsoCode = "475142";
	public final static String FlexStudent = "459607";
	public final static String FlexGraduate = "459608";

	public final static String _szBexhill = "99?999999?99999"; // '-'
	public final static char _cBexhill = '-';
	public final static String _szNorthampton = "999?9?99999999?99"; // '-'
	public final static char _cNorthampton = '-';
	public final static String _szMaidenhead = "999999?999"; // '-'
	public final static char _cMaidenhead = '-';
	public final static String _szBexhill2 = "9999999999999";
	public final static String _szNorthampton2 = "99999999999999";
	public final static String _szMaidenhead2 = "999999999";
	public final static String _szSwindon38 = "999?99999999"; // '/'
	public final static char _cSwindon = '/';
	public final static String _szSwindon39 = "999?Z99999999"; // '/'
	public final static String swindon48NoSepRex = "(\\d{4})(\\d{8})";
	public final static String swindon48NoSepFormatPattern = "$1/$2"; // add the
																		// '/'.
	public final static String _szSwindon48 = "9999?99999999"; // '/'
	public final static String _szSwindon49 = "9999?!99999999"; // '/'
	public final static String _szSwindonSpace38 = "999?99 999 999"; // '/'
	public final static String _szSwindonSpace39 = "999?!99 999 999"; // '/'
	public final static String _szSwindonSpace48 = "9999?99 999 999"; // '/'
	public final static String _szSwindonSpace49 = "9999?!99 999 999"; // '/'
	// added to allow validation of NBS no's without prefix.
	public final static String _szSwindon8NoPfx = "99999999";
	public final static String _szSwindon9NoPfx = "!99999999";
	public final static String _szSwindon8SNoPfx = "99 999 999"; // 'SpaceNoPfx'
	public final static String _szSwindon9SNoPfx = "!99 999 999"; // 'SpaceNoPfx'
	// end of add in.
	public final static String _szSCAN = "999999?99999999"; // ' '
	public final static String _szSCAN_Hyphen = "99-99-99?99999999"; // ' '
	public final static char _cSCAN = ' ';
	public final static String _szVISA_13 = "9999999999999";
	public final static String _szVISA_Space13 = "9999 999 999 999";
	public final static String _szVISA_16 = "9999999999999999";
	public final static String _szVISA_Space16 = "9999 9999 9999 9999";
	public final static char _cPAN = '\0';
	public final static String _szCardmsPAN = "999999999999999999";
	public final static String _szMtgApplNo = "99999999999";
	public final static String _szCashLinkPAN = "5641049996999999999";
	public final static String _szCashLinkPAN_Hy = "564104999699999999?9";
	public final static String _szCashLinkPAN_Hys = "564104?999?699999999?9";
	public final static String _szErrorSuspense = "9999?99999999A"; // '/'
	public final static String _szExtRefNo = "7779999999";
	
	public final static String _szTrvlInsNo = "00000699999999"; //Travel Insurance Acc no - Added as part of SAMM TFR Def#1046
	
	public final static String _szMellonClientNo = "99/999AA999/999";
	public final static String _szMellonClient_LG = "A99/999AA999/999"; // taken
																		// over
																		// by
																		// L&G
	// public final static String _szMellonClient_CutDown = "999AA99999";
	public final static String mellonClientRegex = "69/(\\d{3}[A-Za-z]{2}\\d{3})/\\d(\\d{2})";
	public final static String mellonClientRegexFormatPattern = "$1$2";
	public final static String mellonClientCutDownRegex = "\\d{3}[A-Za-z]{2}\\d{5}";
	public final static String _szUCBMortgageNo = "A?03?9999999";
	// static String _szUCBMortgageNo2 = "88889999999"; // new format.
	public final static char _cUCB = ' ';
	public final static String _szMPOS4 = "A9999"; // MPOS 4 digit no
	public final static String _szMPOS5 = "A99999"; // MPOS 5 digit no
	public final static String _szMPOS6 = "A999999"; // MPOS 6 digit no
	public final static String _szMPOS7 = "A9999999"; // MPOS 7 digit no
	public final static String _szMPOS8 = "A99999999"; // MPOS 8 digit no
	public final static String _szMPOS9 = "A999999999"; // MPOS 9 digit no
	// MPOS 10 digit no - removed as MSO now own this range.
	public final static String _szPROGEN12 = "999999999-99"; // New Progen
																// numbers
	public final static String _szPROGEN14 = "95999999999-99"; // legacy Progen
																// numbers
																// (95nnnnnnnnn-01
																// or -08)
	public final static String _szPersonalLoanAIP = "AA99999999"; // PersonalLoan
																	// Approval
																	// in
																	// Principal
	public final static String _szMellonA = "AAA99999999"; // Mellon type 1
	public final static String _szMellonB = "9A?99999999"; // Mellon type 2
	public final static String _szMellonA_LG = "AAAA99999999"; // taken over by
																// L&G
	public final static String _szMellonB_LG = "A9A?99999999"; // taken over by
																// L&G
	public final static char _cMellon = ' '; // Mellon type 2 separator char.
	public final static String _szCapsil = "AX9999A999"; // Capsil
	public final static String _szCapsil2 = "AX9999A"; // Capsil without segment
														// number
	public final static String _szCapsil_LG = "AAX9999A999"; // taken over by
																// L&G
	public final static String _szCapsil2_LG = "AAX9999A"; // taken over by L&G
	public final static String _szIPOS6 = "A999999"; // IPOS 6 digit no
	public final static String _szIPOS7 = "A9999999"; // IPOS 7 digit no
	public final static String _szIPOS8 = "A99999999"; // IPOS 8 digit no
	public final static String _szIPOS9 = "A999999999"; // IPOS 9 digit no
	public final static String _szIPOS10 = "A9999999999"; // IPOS 10 digit no
	public final static String _szPortman1 = "AW"; // portman 6 char
	public final static String _szPortman2 = "AWW"; // portman 6 char
	public final static String _szPortman3 = "AWWW"; // portman 6 char
	public final static String _szPortman4 = "AWWWW"; // portman 6 char
	public final static String _szPortman5 = "AWWWWW"; // portman 6 char
	public final static String _szPortman6 = "AWWWWWW"; // portman 6 char
	public final static String _szPortman7 = "AWWWWWWW"; // portman 7 char
	public final static String _szPortman8 = "AWWWWWWWW"; // portman 8 char
	public final static String _szPortman9 = "AWWWWWWWWW"; // portman 9 char
	public final static String _szPortman10 = "AWWWWWWWWWW"; // portman 10 char
	public final static String _szPortman11 = "AWWWWWWWWWWW"; // portman 11 char
	public final static String _szPortman12 = "AWWWWWWWWWWWW"; // portman 12
																// char
	public final static String _szPortman13 = "AWWWWWWWWWWWWW"; // portman 13
																// char
	public final static String _szPortman14 = "AWWWWWWWWWWWWWW"; // portman 14
																	// char
	public final static String _szPortman15 = "AWWWWWWWWWWWWWWW"; // portman 15
																	// char
	public final static String _szPortman16 = "AWWWWWWWWWWWWWWWW"; // portman 16
																	// char
	public final static String _szPortman17 = "AWWWWWWWWWWWWWWWWW"; // portman
																	// 17 char
	public final static String _szPortman18 = "AWWWWWWWWWWWWWWWWWW"; // portman
																		// 18
																		// char
	public final static String _szPortman19 = "AWWWWWWWWWWWWWWWWWWW"; // portman
																		// 19
																		// char
	public final static String _szLAndGProtection = "A9999999999"; // L&G
																	// protection
																	// product
	public final static String _szLAndGGEB = "AA999999999"; // L&G GEB number
	public final static String nirvanaMotorRegex = "2[12]\\d{9}"; // LV motor
																	// policy
	public final static String nirvanaTravelRegex = "NBS\\d{1,9}"; // LV travel
																	// policy
	public final static String nirvanaCommercialRegex = "NBS(COM|SHP|OFF|PTY)\\d{8}"; // LV
																						// commercial
																						// policy
	public final static String stakeholderPensionRegex = "NWSHP\\d{7,12}"; // Melon
																			// &
																			// L&G
																			// stakeholder
																			// pensions.
	public final static String coniferSavingsRegex = "[0-9]{2}[A-Z]{1}[0-9]{7}[A-Z][A-Z ]{0,2}"; // "99A9999999AAA"
																									// Conifer
																									// (third
																									// party)
																									// account.
	public final static String newmanPolicyRegex = "^NBS[A-Za-z]{2}[0-9]{9}$"; // Newman
																				// protection
																				// policy.
	public final static String RBSIGeneralInsuranceRegex = "^R[0-9]{8}$"; // RBSI
																			// General
																			// Insurance
																			// 8
																			// digits
																			// prefixed
																			// with
																			// R

	public final static String cheshireBuildingSocietyRegex = "CBS\\d{9,11}";     	// cheshire building society
	public final static String derbyshireBuildingSocietyRegex = "DBS\\d{8,9}";    	// derbyshire building society
	public final static String dunfermlineBuildingSocietyRegex = "DUBS\\d{8,9}"; 	// dunfermline building society
	public final static String tsysIdRegex = "CC[0-9]{11}";							// TSYS Id 11 digits prefixed with CC
    
	
	static final TagSORTYPE Flex1SortType = new TagSORTYPE("070116",
			Constants.FLEXACCOUNT);
	static final TagSORTYPE Flex2SortType = new TagSORTYPE("074456",
			Constants.FLEXACCOUNT);
	static final TagSORTYPE CashbuilderSortType = new TagSORTYPE("070070",
			Constants.PERSONALOAN);
	static final TagSORTYPE PersonalLoanSortType = new TagSORTYPE("070030",
			Constants.CASHBUILDER);
	static final TagSORTYPE CredInsSortType = new TagSORTYPE("070071",
			Constants.CREDINSURN);
	static final TagSORTYPE RBSInvestSortType = new TagSORTYPE("070072",
			Constants.RBSINVEST);
	static final TagSORTYPE BusInvestSortType = new TagSORTYPE("070055",
			Constants.BUSINVEST);
	static final TagSORTYPE PortInvestSortType = new TagSORTYPE("070066",
			Constants.PORTINVEST);
	static final TagSORTYPE AccountRenumberFlex1SortType = new TagSORTYPE(
			"070436", Constants.NEWFLEX);
	static final TagSORTYPE AccountRenumberFlex2SortType = new TagSORTYPE(
			"071226", Constants.NEWFLEX);
	static final TagSORTYPE AccountRenumberFlex3SortType = new TagSORTYPE(
			"071096", Constants.NEWFLEX);
	static final TagSORTYPE AccountRenumberFlex4SortType = new TagSORTYPE(
			"070246", Constants.NEWFLEX);
	static final TagSORTYPE AccountRenumberFlex5SortType = new TagSORTYPE(
			"070976", Constants.NEWFLEX);
	static final TagSORTYPE AccountRenumberFlex6SortType = new TagSORTYPE(
			"071986", Constants.NEWFLEX);
	static final TagSORTYPE AccountRenumberFlex7SortType = new TagSORTYPE(
			"071306", Constants.NEWFLEX);
	static final TagSORTYPE AccountRenumberFlex8SortType = new TagSORTYPE(
			"070806", Constants.NEWFLEX);
	static final TagSORTYPE AccountRenumberCard1SortType = new TagSORTYPE(
			"071040", Constants.NEWCARD);
	static final TagSORTYPE AccountRenumberCard2SortType = new TagSORTYPE(
			"071120", Constants.NEWCARD);
	static final TagSORTYPE AccountRenumberCard3SortType = new TagSORTYPE(
			"071310", Constants.NEWCARD);
	static final TagSORTYPE AccountRenumberCard4SortType = new TagSORTYPE(
			"071350", Constants.NEWCARD);
	static final TagSORTYPE AccountRenumberCard5SortType = new TagSORTYPE(
			"071490", Constants.NEWCARD);
	static final TagSORTYPE AccountRenumberCard6SortType = new TagSORTYPE(
			"071520", Constants.NEWCARD);
	static final TagSORTYPE AccountRenumberCard7SortType = new TagSORTYPE(
			"071660", Constants.NEWCARD);

	static final TagSORTYPE[] stabSortCodeType = new TagSORTYPE[] {
			Flex1SortType, Flex2SortType, PersonalLoanSortType,
			CashbuilderSortType, CredInsSortType, RBSInvestSortType,
			BusInvestSortType, PortInvestSortType,
			AccountRenumberFlex1SortType, AccountRenumberFlex2SortType,
			AccountRenumberFlex3SortType, AccountRenumberFlex4SortType,
			AccountRenumberFlex5SortType, AccountRenumberFlex6SortType,
			AccountRenumberFlex7SortType, AccountRenumberFlex8SortType,
			AccountRenumberCard1SortType, AccountRenumberCard2SortType,
			AccountRenumberCard3SortType, AccountRenumberCard4SortType,
			AccountRenumberCard5SortType, AccountRenumberCard6SortType,
			AccountRenumberCard7SortType };

	static final int nCountSortCodeType = stabSortCodeType.length;

	static final String[] newSortCodes = new String[] { "070436", "071226",
			"071096", "070246", "070976", "071986", "071306", "070806",
			"071040", "071120", "071310", "071350", "071490", "071520",
			"071660" };
	static final String[] SuspenseNo = new String[] { "64999999E", "69999999E",
			"79999999E", "99999999E" };
	static final int nCountSuspenseNo = SuspenseNo.length;
}
