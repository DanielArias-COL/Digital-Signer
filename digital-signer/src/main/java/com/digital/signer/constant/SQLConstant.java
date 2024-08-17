package com.digital.signer.constant;

public class SQLConstant {

    public static final String SAVE_USER = "INSERT INTO public.user_ds (username, \"password\") VALUES(?, ?) RETURNING id";
}
