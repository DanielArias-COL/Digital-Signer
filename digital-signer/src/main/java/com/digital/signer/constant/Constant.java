package com.digital.signer.constant;

public class Constant {

    public static final String START = "Starting method : {0}";

    public static final String END = "Ending method : {0}";

    public static final String CREATE_USER = "createUser: ";

    public static final String ADD_FILES = "addFiles ";

    public static final String GENERATE_KEY_PAIR = "generateKeyPairForUser ";

    public static final String SING_IN = "singIn: ";

    public static final String LIST_FILES = "listFilesForUser ";

    public static final String SAVE_FILES = "saveFiles ";

    public static final String SIGNED_FILE = "SignedFile ";

    public static final String VERIFY_FILE = "verifyFile ";

    /** Error list */
    public static final String ERROR_CODE_200 = "200";
    public static final String ERROR_MESSAGE_200 = "Successful process";

    public static final String ERROR_CODE_401 = "401";
    public static final String ERROR_MESSAGE_401 = "Invalid user credentials";

    public static final String ERROR_CODE_402 = "402";
    public static final String ERROR_MESSAGE_402 = "Files not found";

    public static final String ERROR_CODE_403 = "403";
    public static final String ERROR_MESSAGE_403 = "Error during saving files";

    public static final String ERROR_CODE_405 = "405";
    public static final String ERROR_MESSAGE_405 = "Error during file confirmation";

    public static final String ERROR_CODE_406 = "406";
    public static final String ERROR_MESSAGE_406 = "Error file has been modified";

    public static final String ERROR_CODE_407 = "407";
    public static final String ERROR_MESSAGE_407 = "The error file is correct, but the digital signature is missing";

    public static final String ERROR_CODE_408 = "408";
    public static final String ERROR_MESSAGE_408 = "File signature does not match";

}
