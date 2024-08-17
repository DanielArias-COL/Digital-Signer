package com.digital.signer.constant;

public class SQLConstant {

    public static final String SAVE_USER = "INSERT INTO public.user_ds (username, \"password\") VALUES(?, ?) RETURNING id";

    public static final String SAVE_KEY = "INSERT INTO public.\"key\" (\"key\") VALUES(?) RETURNING id";

    public static final String SAVE_USER_KEY = "INSERT INTO public.user_key (id_user, id_key) VALUES(?, ?)";
}
