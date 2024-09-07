package com.digital.signer.constant;

public class Constant {

    public static final String START = "Starting method : {0}";

    public static final String END = "Ending method : {0}";

    public static final String CREATE_USER = "createUser: ";

    public static final String ADD_FILES = "addFiles ";

    public static final String GENERATE_KEY_PAIR = "generateKeyPairForUser ";

    public static final String SING_IN = "singIn: ";

    public static final String GOOGLE_SING_IN = "googleSingIn: ";

    public static final String LIST_FILES = "listFilesForUser ";

    public static final String LIST_SHARE_FILES = "listMySharesFile ";

    public static final String SAVE_FILES = "saveFiles ";

    public static final String SIGNED_FILE = "SignedFile ";

    public static final String SIGN_SHARING_FILE = "signSharingFile ";

    public static final String VERIFY_FILE = "verifyFile ";

    public static final String SHARE_FILE = "shareFile ";

    public static final String LIST_SHARE_USERS = "shareFile ";

    /** Error list */
    public static final String ERROR_CODE_200 = "200";
    public static final String ERROR_MESSAGE_200 = "Successful process";

    public static final String ERROR_CODE_401 = "401";
    public static final String ERROR_MESSAGE_401 = "Invalid user credentials";

    public static final String ERROR_CODE_402 = "402";
    public static final String ERROR_MESSAGE_402 = "Files not found";

    public static final String ERROR_CODE_403 = "403";
    public static final String ERROR_MESSAGE_403 = "Error during saving files";

    public static final String ERROR_CODE_404 = "404";
    public static final String ERROR_MESSAGE_404 = "Error during singing file";

    public static final String ERROR_CODE_405 = "405";
    public static final String ERROR_MESSAGE_405 = "Error during file confirmation";

    public static final String ERROR_CODE_406 = "406";
    public static final String ERROR_MESSAGE_406 = "Error file has been modified";
    public static final String ERROR_STATE_406 = "El archivo a sido modificado";

    public static final String ERROR_CODE_407 = "407";
    public static final String ERROR_STATE_407 = "I-F";  //it have integrity but not signature
    public static final String ERROR_MESSAGE_407 = "The error file is correct, but the digital signature is missing";

    public static final String ERROR_CODE_408 = "408";
    public static final String ERROR_MESSAGE_408 = "File signature does not match";

    public static final String ERROR_CODE_409 = "409";
    public static final String ERROR_MESSAGE_409 = "Error sharing files";

    public static final String ERROR_CODE_410 = "410";
    public static final String ERROR_MESSAGE_410 = "Error getting user to share file";

    public static final String ERROR_STATE_200 = "IFV"; //it have integrity It's Signature and It's Verified
    public static final String ERROR_STATE_408 = "IF-V"; // it have integrity It's Signature but it is not verified
}
