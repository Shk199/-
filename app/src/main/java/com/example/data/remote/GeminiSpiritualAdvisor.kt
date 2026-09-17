package com.example.data.remote

import com.example.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

object GeminiSpiritualAdvisor {

    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    /**
     * طلب استشارة تربوية أو خطوة عملية من مستشار التزكية
     */
    suspend fun getSpiritualAdvice(
        topic: String,
        userContext: String
    ): Result<String> = withContext(Dispatchers.IO) {
        val apiKey = try {
            BuildConfig.GEMINI_API_KEY
        } catch (e: Exception) {
            ""
        }

        if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
            // Authentic rich local spiritual advice fallback
            return@withContext Result.success(getLocalFallbackAdvice(topic))
        }

        try {
            val systemInstruction = "أنت مستشار أخلاقي وروحي إسلامي في تطبيق 'راقب' للتزكية والرقابة الذاتية. مهمتك تقديم نصيحة عملية رقيقة مبنية على القرآن الكريم والسنة النبوية الصحيحة، مع خطوات تطبيقية محددة وقابلة للتنفيذ في اليوم الحالي، بعيداً عن التشدد وبالتركيز على تهذيب النفس وعلاج الفتور وكظم الغيظ وحفظ اللسان باللغة العربية الفصحى الجميلة والموجزة."

            val prompt = "الموضوع: $topic\nملاحظة أو حالة المستخدم: $userContext\nقدم لي نصيحة تربوية وخطوة عملية واضحة لتزكية النفس اليوم في ضوء هذا الموقف."

            val requestJson = JSONObject().apply {
                put("contents", JSONArray().apply {
                    put(JSONObject().apply {
                        put("parts", JSONArray().apply {
                            put(JSONObject().apply {
                                put("text", "$systemInstruction\n\n$prompt")
                            })
                        })
                    })
                })
            }

            val requestBody = requestJson.toString().toRequestBody("application/json".toMediaType())
            val url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent?key=$apiKey"

            val request = Request.Builder()
                .url(url)
                .post(requestBody)
                .build()

            val response = client.newCall(request).execute()
            if (response.isSuccessful) {
                val responseBody = response.body?.string().orEmpty()
                val rootJson = JSONObject(responseBody)
                val candidates = rootJson.optJSONArray("candidates")
                if (candidates != null && candidates.length() > 0) {
                    val firstCandidate = candidates.getJSONObject(0)
                    val content = firstCandidate.optJSONObject("content")
                    val parts = content?.optJSONArray("parts")
                    val text = parts?.optJSONObject(0)?.optString("text")
                    if (!text.isNullOrBlank()) {
                        return@withContext Result.success(text.trim())
                    }
                }
            }
            Result.success(getLocalFallbackAdvice(topic))
        } catch (e: Exception) {
            Result.success(getLocalFallbackAdvice(topic))
        }
    }

    private fun getLocalFallbackAdvice(topic: String): String {
        return when {
            topic.contains("غضب") || topic.contains("غيظ") ->
                "وصية نبوية جامعة: قال رجل للنبي ﷺ: أوصني، قال: «لا تغضب» فردد مراراً قال: «لا تغضب».\n\nخطوات عملية فورية للتطبيق اليوم:\n١. إذا غضبت فاستعذ بالله وتوقف عن الحديث تماماً لمدة دقيقة كاملة.\n٢. غيّر هيئتك: إن كنت قائماً فاجلس، وإن كنت جالساً فاضطجع أو توضأ بالماء البارد فالغضب جمرة يطفئها الماء.\n٣. تذكر ثواب كظم الغيظ: «من كظم غيظاً وهو يستطيع أن ينفذه دعاه الله على رؤوس الخلائق حتى يخيّره من أي الحور شاء»."

            topic.contains("لسان") || topic.contains("غيبة") || topic.contains("كلام") ->
                "اللسان أصغر الأعضاء جرماً وأعظمها جرماً. قال معاذ: أوَ إنا لمؤاخذون بما نتكلم به؟ فقال ﷺ: «وهل يكب الناس في النار على وجوههم إلا حصائد ألسنتهم؟».\n\nخطوات عملية للتطبيق اليوم:\n١. طبّق قاعدة الـ 5 ثوانٍ قبل التحدث: هل ما ستقوله خير؟ هل فيه ذكر لله أو نفع لأحد؟ إن لم يكن، فالصمت عبادة.\n٢. إذا بدأ أحد في مجلسك بذكر غائب، بادر بلباقة إلى تغيير الموضوع أو ذبّ عن عرض أخيك بالحق.\n٣. اجعل كفارة كل كلمة لغو الاستغفار عشر مرات فوراً."

            topic.contains("فجر") || topic.contains("صلاة") || topic.contains("كسل") ->
                "صلاة الفجر هي ميزان الإخلاص وعنوان النشاط الروحي. قال ﷺ: «ركعتا الفجر خير من الدنيا وما فيها».\n\nخطوات عملية للاستيقاظ بحول الله:\n١. التبكير في النوم وتجنب الشاشات الزرقاء قبل النوم بنصف ساعة مع قراءة أذكار النوم.\n٢. وضع المنبه في مكان بعيد عن متناول اليد في الغرفة حتى تضطر للقيام من فراشك.\n٣. النية الصادقة عند إغلاق العينين بأنك ستقف بين يدي الله حباً ورغبة فيما عنده."

            topic.contains("صلة") || topic.contains("رحم") || topic.contains("أهل") ->
                "الرحم معلقة بالعرش تقول: «من وصلني وصله الله، ومن قطعني قطعه الله». والواصل ليس بالمكافئ، بل من إذا قُطعت رحمه وصلها.\n\nخطوات عملية للتطبيق اليوم:\n١. اختر قريباً بينك وبينه جفاء أو قطيعة وابدأ بإرسال رسالة سلام ودعاء خالصة لوجه الله.\n٢. لا تنتظر المبادرة من الطرف الآخر؛ فالأجر في السبق إلى الخير.\n٣. اجعل نيتك إرضاء الله وحده وسلامة صدرك من أي حرج."

            else ->
                "يا طالب التزكية، تذكر أن النفس كالمهر الجامح تحتاج إلى رفق ومجاهدة مستمرة: «والذين جاهدوا فينا لنهدينهم سبلنا».\n\nخطوات للتطبيق اليوم:\n١. حاسب نفسك بنية التطهير لا القنوط، فباب التوبة مفتوح في كل لحظة.\n٢. لا تحقرن من المعروف شيئاً، فرب ابتسامة في وجه محزون أو تسبيحة في خلوة ترفعك عند الله درجات.\n٣. الزم الاستغفار دوماً فإنه يزيل ران القلوب ويجدد شعلة العزيمة."
        }
    }
}
