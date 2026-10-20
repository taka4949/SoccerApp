
package com.example.soccerapp.data.local

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec



//Ktorから受け取ったログイントークンを暗号化したり、元に戻したりするための処理をまとめてある。


class TokenCipher {

    private val keyStore =
        KeyStore.getInstance("AndroidKeyStore").apply {
            load(null)//読み込み時に追加の設定パラメータを渡さない
        }

    private fun getKey(): SecretKey? {
        return keyStore.getKey(KEY_ALIAS, null) as? SecretKey
    }




    private fun createKey(): SecretKey {//getKey() で鍵を取得できなかったとき利用。

        val keyGenerator = KeyGenerator.getInstance(//鍵の生成クラス
            KeyProperties.KEY_ALGORITHM_AES,//AES方式
            "AndroidKeyStore"
        )



        val spec = KeyGenParameterSpec.Builder(//キーの名前指定、暗号化＋復号化許可
            KEY_ALIAS,
            KeyProperties.PURPOSE_ENCRYPT or
                    KeyProperties.PURPOSE_DECRYPT
        )

            .setKeySize(256)
            .setBlockModes(KeyProperties.BLOCK_MODE_GCM)//GCM:暗号文が改ざんされていないか検証できる
            .setEncryptionPaddings(
                KeyProperties.ENCRYPTION_PADDING_NONE
            )
            .build()

        keyGenerator.init(spec)
        return keyGenerator.generateKey()
    }





    fun encrypt(token: String): String {//暗号化→文字列として返す

        val key = getKey() ?: createKey()//左側が null でなければ左側の値を使用し、null なら右側の処理を実行




        val cipher = Cipher.getInstance(TRANSFORMATION)


        cipher.init(Cipher.ENCRYPT_MODE, key)

        val encrypted = cipher.doFinal(
            token.toByteArray(Charsets.UTF_8)
        )



        val iv = Base64.encodeToString(//暗号化時の補助値
            cipher.iv,
            Base64.NO_WRAP
        )

        val data = Base64.encodeToString(//暗号文
            encrypted,
            Base64.NO_WRAP
        )

        return "$iv:$data"//合体して１つにする。
    }







    fun decrypt(stored: String): String {//復号化
        val key = getKey()
            ?: throw IllegalStateException(
                "Encryption key is unavailable"
            )

        val parts = stored.split(":")
        require(parts.size == 2) {//iv+data
            "Invalid encrypted token format"
        }



        val iv = Base64.decode(parts[0], Base64.NO_WRAP)
        val encrypted = Base64.decode(
            parts[1],
            Base64.NO_WRAP
        )//バイト配列に戻す



        val cipher = Cipher.getInstance(TRANSFORMATION)

        cipher.init(//実行
            Cipher.DECRYPT_MODE,
            key,
            GCMParameterSpec(128, iv)
        )

        return String(
            cipher.doFinal(encrypted),
            Charsets.UTF_8
        )
    }


    companion object {//鍵の名前、方式を指定（暗号化と復号化は共通）
        private const val KEY_ALIAS = "soccerapp_token_key"
        private const val TRANSFORMATION = "AES/GCM/NoPadding"
    }
}
