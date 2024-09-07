package com.digital.signer.constant;

public class SQLConstant {

    public static final String SAVE_USER = "INSERT INTO public.user_ds (username, \"password\", email) VALUES(?, ?, ?) RETURNING id";

    public static final String SAVE_KEY = "INSERT INTO public.\"key\" (\"key\", is_public) VALUES(?, ?) RETURNING id";

    public static final String SAVE_USER_KEY = "INSERT INTO public.user_key (id_user, id_key) VALUES(?, ?)";

    public static final String SELECT_USER_INFO = "SELECT id FROM public.user_ds WHERE username = ? AND password = ?";

    public static final String SELECT_USER_GOOGLE_INFO = "SELECT id FROM public.user_ds WHERE email = ?";

    public static final String SELECT_USER_FILES = "SELECT id, \"name\", bytes, integrity_hash, user_ds_id, digital_signed " +
            "FROM public.file WHERE user_ds_id = ?";

    public static final String SELECT_SHARE_FILES = "SELECT pf.id, \"name\", pf.bytes, pf.integrity_hash, pf.user_ds_id, fls.digital_signer_target FROM public.file_share fls " +
            "JOIN public.file pf on pf.id = fls.id_file " +
            "WHERE fls.id_user_target = ?";

    public static final String SELECT_FILE = "SELECT integrity_hash FROM public.file WHERE id = ?";

    public static final String EXIST_FILE = "SELECT EXISTS (SELECT 1 FROM public.file WHERE id = ?)";

    public static final String EXIST_USER = "SELECT EXISTS (SELECT 1 FROM public.user_ds WHERE email = ?)";

    public static final String EXIST_USER_KEY = "select \"key\" from public.user_key uk " +
            "join public.\"key\" k on k.id = uk.id_key " +
            "where id_user = ? and is_public = false";

    public static final String USER_DIGITAL_SIGNED = "UPDATE public.file SET digital_signed = ? " +
            "WHERE id = ?;";

    public static final String USER_DIGITAL_SHARE_SIGNED = "UPDATE public.file_share SET digital_signer_target=? WHERE id_file = ? and id_user_target = ? and id_user_source = ?";

    public static final String ADD_FILE = "INSERT INTO public.file " +
            "(\"name\", bytes, integrity_hash, user_ds_id) " +
            "VALUES(?, ?, ?, ?)";

    public static final String SELECT_CONFIRM_FILE = "SELECT integrity_hash, digital_signed, bytes, k.\"key\" FROM public.file f " +
            "JOIN public.user_key uk on uk.id_user = ? " +
            "JOIN public.\"key\" k on k.id = uk.id_key and k.is_public = true " +
            "WHERE f.id = ?";

    public static final String SAVE_SHARE_FILE = "INSERT INTO public.file_share (id_user_source, id_user_target, id_file) VALUES(?, ?, ?)";

    public static final String SELECT_SHARE_USERS = "SELECT id, email FROM user_ds ud";

    public static final String SELECT_SHARE_USERS_SIGNED = "select ud.email, ud.id from public.file_share fs2 " +
            "join user_ds ud on ud.id = fs2.id_user_target " +
            "where fs2.digital_signer_target is not null " +
            "and fs2.id_file = ? " +
            "and fs2.id_user_source = ?";


    public static final String SELECT_USER_EMAIL = "SELECT email FROM public.user_ds WHERE id = ?";

}
