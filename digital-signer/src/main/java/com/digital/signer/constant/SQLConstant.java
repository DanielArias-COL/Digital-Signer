package com.digital.signer.constant;

public class SQLConstant {

    public static final String SAVE_USER = "INSERT INTO public.user_ds (username, \"password\") VALUES(?, ?) RETURNING id";

    public static final String SAVE_KEY = "INSERT INTO public.\"key\" (\"key\") VALUES(?) RETURNING id";

    public static final String SAVE_USER_KEY = "INSERT INTO public.user_key (id_user, id_key) VALUES(?, ?)";

    public static final String SELECT_USER_INFO = "SELECT id FROM public.user_ds WHERE username = ? AND password = ?";

    public static final String SELECT_USER_FILES = "SELECT id, \"name\", bytes, integrity_hash, user_ds_id, digital_signed " +
            "FROM public.file WHERE user_ds_id = ?";

    public static final String SELECT_FILE = "SELECT integrity_hash FROM public.file WHERE id = ?";

    public static final String EXIST_FILE = "SELECT EXISTS (SELECT 1 FROM public.file WHERE id = ?)";

    public static final String USER_DIGITAL_SIGNED = "UPDATE public.file SET digital_signed = ? " +
            "WHERE id = ?;";

    public static final String ADD_FILE = "INSERT INTO public.file " +
            "(\"name\", bytes, integrity_hash, user_ds_id) " +
            "VALUES(?, ?, ?, ?)";

    public static final String SELECT_CONFIRM_FILE = "SELECT integrity_hash, digital_signed, bytes, k.\"key\" FROM public.file f " +
            "JOIN public.user_key uk on uk.id_user = ? " +
            "JOIN public.\"key\" k on k.id = uk.id_key " +
            "WHERE f.id = ?";

}
