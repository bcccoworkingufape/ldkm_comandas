package com.gps.ludke.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.renderscript.ScriptC;

import androidx.annotation.Nullable;

public class DadosOpenHelper extends SQLiteOpenHelper {

    public DadosOpenHelper(@Nullable Context context) {
        super(context,"DADOS",null,4);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(ScriptDLL.getCreateTableProduto());
        sqLiteDatabase.execSQL(ScriptDLL.getCreateTableCliente());
        sqLiteDatabase.execSQL(ScriptDLL.getCreateTableComanda());
        sqLiteDatabase.execSQL(ScriptDLL.getCreateTableVenda());
        //sqLiteDatabase.execSQL(ScriptDLL.dropTables());
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL(ScriptDLL.getCreateTableProduto());
        sqLiteDatabase.execSQL(ScriptDLL.getCreateTableCliente());
        sqLiteDatabase.execSQL(ScriptDLL.getCreateTableComanda());
        sqLiteDatabase.execSQL(ScriptDLL.getCreateTableVenda());
    }


}
