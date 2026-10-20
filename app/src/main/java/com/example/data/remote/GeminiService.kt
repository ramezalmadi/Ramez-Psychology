package com.example.data.remote

import com.example.BuildConfig
import com.squareup.moshi.JsonClass
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

@JsonClass(generateAdapter = true)
data class GeminiPart(
    val text: String? = null
)

@JsonClass(generateAdapter = true)
data class GeminiContent(
    val parts: List<GeminiPart>,
    val role: String? = null
)

@JsonClass(generateAdapter = true)
data class GeminiRequest(
    val contents: List<GeminiContent>,
    val systemInstruction: GeminiContent? = null
)

@JsonClass(generateAdapter = true)
data class GeminiCandidate(
    val content: GeminiContent?
)

@JsonClass(generateAdapter = true)
data class GeminiResponse(
    val candidates: List<GeminiCandidate>?
)

interface GeminiApi {
    @POST("v1beta/models/gemini-3.5-flash:generateContent")
    suspend fun generateContent(
        @Query("key") apiKey: String,
        @Body request: GeminiRequest
    ): GeminiResponse
}

object GeminiClient {
    private const val BASE_URL = "https://generativelanguage.googleapis.com/"

    private val moshi = Moshi.Builder()
        .addLast(KotlinJsonAdapterFactory())
        .build()

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        })
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .build()

    val api: GeminiApi = retrofit.create(GeminiApi::class.java)

    suspend fun askApix(prompt: String, conversationHistory: List<Pair<String, Boolean>> = emptyList()): String =
        withContext(Dispatchers.IO) {
            val apiKey = BuildConfig.GEMINI_API_KEY
            if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
                return@withContext fallbackApixResponse(prompt)
            }

            val contents = mutableListOf<GeminiContent>()
            conversationHistory.takeLast(6).forEach { (msg, isUser) ->
                contents.add(
                    GeminiContent(
                        role = if (isUser) "user" else "model",
                        parts = listOf(GeminiPart(text = msg))
                    )
                )
            }
            contents.add(
                GeminiContent(
                    role = "user",
                    parts = listOf(GeminiPart(text = prompt))
                )
            )

            val systemPrompt = """
                أنت "Apix" (أبيكس)، المرشد النفسي والذكاء الاصطناعي الخاص بتطبيق "Ramez Psychology" لكتاب علم النفس السلوكي للدكتور رامز (افهم سلوكك.. اصنع مستقبلك).
                شخصيتك: دافئ، حكيم، داعم، تحليلي بأسلوب علم النفس السلوكي، وتربط الإجابات بتطوير العادات، كسر التسويف، وضع الحدود، الذكاء العاطفي، وحماية الطاقة النفسية.
                إذا طلب المستخدم أمراً تنفيذياً (مثل ضبط منبه، تغيير الخطة، إضافة مهمة، الانتقال لقسم)، نفذ الطلب واذكره بوضوح ولطف.
                أجب دائماً بلغة المستخدم (العربية ما لم يسأل بالإنجليزية).
            """.trimIndent()

            try {
                val response = api.generateContent(
                    apiKey = apiKey,
                    request = GeminiRequest(
                        contents = contents,
                        systemInstruction = GeminiContent(parts = listOf(GeminiPart(text = systemPrompt)))
                    )
                )
                val reply = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                if (!reply.isNullOrBlank()) {
                    reply
                } else {
                    fallbackApixResponse(prompt)
                }
            } catch (e: Exception) {
                fallbackApixResponse(prompt)
            }
        }

    fun fallbackApixResponse(prompt: String): String {
        val lower = prompt.lowercase()
        return when {
            lower.contains("منبه") || lower.contains("عير") || lower.contains("alarm") || lower.contains("timer") ->
                "تم فهم طلب المنبه! يمكنك تعييره بالساعات والدقائق والثواني، وسينبهك التطبيق مع مراعاة وضع الرنين أو الصامت."
            lower.contains("خطة") || lower.contains("اسبوع") || lower.contains("يومي") || lower.contains("شهر") ->
                "حسناً! تم توجيه طلبك لتنظيم الخطة في قسم الخطط والمهام. هل ترغب في جعلها خطة أسبوعية أم شهرية وتكرار مهامك؟"
            lower.contains("ضيف") || lower.contains("مهمة") || lower.contains("إضافة") ->
                "أهلاً بك! يمكنك إضافة المهمة مباشرة وستُرتب بتسلسل منظم مع خيارات [تم - تسويف - إلغاء] ومؤقت لإنجازها."
            lower.contains("مشاعر") || lower.contains("حزن") || lower.contains("تعب") || lower.contains("غضب") ->
                "أشعر بك تماماً. في علم النفس السلوكي: 'المشاعر ليست أعداء، بل رسائل تحتاج قبولاً وتفريغاً آمناً'. يمكنك تسجيل ما تشعر به في قسم المشاعر وسأكون دائماً بجانبك."
            lower.contains("كتاب") || lower.contains("رامز") || lower.contains("ramez") ->
                "كتاب علم النفس السلوكي للدكتور رامز متاح بالكامل في قسم الكتاب. مبدأه الأساسي: 'العادة هي جسر بين نية الإنسان ومستقبله الحقيقي'. تصفح الفصول الأربعة لاستكشاف 200 مبدأ سلوكي!"
            else ->
                "أهلاً بك! أنا أبيكس (Apix)، مرشدك النفسي في تطبيق Ramez Psychology. أنا هنا لأساعدك في الإجابة على أي سؤال في حياتك، إدارة عاداتك، تنظيم خططك اليومية، والتغلب على التسويف."
        }
    }
}
