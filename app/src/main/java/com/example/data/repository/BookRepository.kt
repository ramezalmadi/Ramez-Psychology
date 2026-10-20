package com.example.data.repository

import com.example.data.model.BookChapter
import com.example.data.model.CaseStudy

object BookRepository {

    val bookTitle = "علم النفس السلوكي"
    val bookSubtitle = "افهم سلوكك .. اصنع مستقبلك"
    val author = "R A M E Z"
    val motto = "العادة هي جسر بين نية الإنسان ومستقبله الحقيقي"

    val chapters = listOf(
        BookChapter(
            id = 1,
            title = "الفصل الأول: كيف تتعامل مع الناس",
            subtitle = "ديناميكيات التفاعل البشري",
            caseRange = "1 – 50",
            description = "لغة العيون والنظرات، المساحة الشخصية والتحيات، ديناميكية الحديث وفن الاستماع، والتعامل مع الطاقة السلبية."
        ),
        BookChapter(
            id = 2,
            title = "الفصل الثاني: كيف تقرأ نوايا الناس وتكتشف التلاعب",
            subtitle = "كشف التناقضات وحماية النفس",
            caseRange = "51 – 100",
            description = "قراءة لغة الجسد، كشف الكذب والابتزاز العاطفي، النوايا الخفية، التعامل مع الشخصيات السامة وبناء حصانة نفسية."
        ),
        BookChapter(
            id = 3,
            title = "الفصل الثالث: كيف تبني ثقة الناس بك وتؤثر فيهم بشكل إيجابي",
            subtitle = "فن الإقناع والقيادة الملهمة",
            caseRange = "101 – 150",
            description = "بناء الثقة من الصفر، التواصل المؤثر، لغة الجسد الإيجابية، الذكاء العاطفي، حل النزاعات وبناء السمعة الطيبة."
        ),
        BookChapter(
            id = 4,
            title = "الفصل الرابع: كيف تتعامل مع نفسك",
            subtitle = "إدارة المشاعر والطاقة والتطور الشخصي",
            caseRange = "151 – 200",
            description = "إدارة الغضب والحزن والقلق، الحوار الداخلي، التعافي من الاحتراق النفسي، كسر التسويف وتطوير العادات الإيجابية."
        )
    )

    val sampleCases = listOf(
        CaseStudy(
            number = 1,
            question = "إذا نظر إليك أحدهم بنظرة غاضبة أو حادة فجأة، ماذا تفعل؟",
            behavioralAnalysis = "الغضب المفاجئ غالباً يكون 'رد فعل دفاعي' (Defensive Reflex) لشعور الشخص بأنك انتهكت مساحة له، أو أسأت فهمه، أو أنه يمر بيوم سيء وأنت 'الهدف الأسهل' لتفريغ شحنته.",
            practicalResponse = "لا ترد بنفس الحدة (هذا يزيد التصعيد). حافظ على استرخاء كتفيك (لترسل لجسدك إشارة الأمان). انظر إلى منطقة 'المثلث الاجتماعي' (بين عينيه وأنفه) بدلاً من التحديق المباشر. قل بصوت هادئ ومنخفض: 'ألاحظ أنك منزعج، هل حدث شيء أم يمكنني مساعدتك؟'.",
            psychologicalPrinciple = "كسر شرطية 'المثير والاستجابة' (Stimulus-Response) وإزالة التصعيد (De-escalation).",
            chapterId = 1
        ),
        CaseStudy(
            number = 3,
            question = "إذا كان شخص يتحدث معك وعيناه تشردان وتنظران في كل مكان إلا عينيك؟",
            behavioralAnalysis = "هذا ليس بالضرورة إهانة لك. في علم النفس العصبي، الدماغ يقطع التواصل البصري ليقلل 'الحمل المعرفي' (Cognitive Load) عندما يحاول تذكر معلومة أو صياغة فكرة معقدة.",
            practicalResponse = "لا تقل له 'انظر إلي عندما أتحدث'. بل أوقف الحديث لثانيتين حتى يعيد عقله التركيز، أو اطرح سؤالاً مباشراً وبسيطاً لإعادته للمسار.",
            psychologicalPrinciple = "إدارة الحمل المعرفي (Cognitive Load Management).",
            chapterId = 1
        ),
        CaseStudy(
            number = 11,
            question = "إذا قاطعك شخص باستمرار أثناء حديثك؟",
            behavioralAnalysis = "المقاطعة المستمرة تعني أن الشخص يعاني من 'اندفاعية' (Impulsivity) أو يشعر أن فكرته أهم، أو أنه لم يتعلم 'تناوب الأدوار' في الطفولة.",
            practicalResponse = "لا ترفع صوتك. استخدم تقنية 'التوقف واليد'. توقف عن الحديث فوراً، ارفع يدك بلطف كإشارة 'توقف'، وقل: 'دعني أكمل فكرتي، ثم أسمع رأيك بالكامل'. إذا قاطع مرة أخرى، قل: 'لم أنته بعد'.",
            psychologicalPrinciple = "التكييف الإجرائي (Operant Conditioning) لوضع حدود للحديث.",
            chapterId = 1
        ),
        CaseStudy(
            number = 21,
            question = "إذا طلب منك شخص قرضاً مالياً وأنت لا تريد؟",
            behavioralAnalysis = "طلب المال يضع العلاقات تحت ضغط 'الابتزاز العاطفي'. الرفض المباشر قد يقطع العلاقة، والقبول يدمرك.",
            practicalResponse = "استخدم قاعدة 'الرفض بدون اعتذار مفرط'. قل: 'أنا أقدر ثقتك بي، لكنني وضعت قاعدة شخصية بعدم إقراض أو اقتراض المال من الأصدقاء/العائلة للحفاظ على علاقتنا'.",
            psychologicalPrinciple = "وضع القاعدة المسبقة (Pre-emptive Boundary Setting).",
            chapterId = 1
        ),
        CaseStudy(
            number = 51,
            question = "إذا قال لك شخص 'أنا موافق' لكن ذراعيه معقودتان على صدره، ماذا تفعل؟",
            behavioralAnalysis = "الذراعان المعقودتان هما 'حاجز دفاعي' (Defensive Barrier) يدل على الانغلاق أو عدم الراحة. هناك تناقض بين الكلام (موافقة) والجسد (رفض).",
            practicalResponse = "لا تصدق الكلام فوراً. قل: 'ألاحظ أنك قد تكون غير مرتاح تماماً، هل هناك شيء يقلقك؟'. هذا يمنحه فرصة للتعبير عن تحفظاته الحقيقية.",
            psychologicalPrinciple = "قراءة التناقض السلوكي (Behavioral Incongruence Detection).",
            chapterId = 2
        ),
        CaseStudy(
            number = 61,
            question = "إذا قال لك شخص 'إذا كنت تحبني حقاً، ستفعل هذا'؟",
            behavioralAnalysis = "هذا 'ابتزاز عاطفي صريح' (Explicit Emotional Blackmail) يربط الحب بالطاعة.",
            practicalResponse = "لا تقبل الربط. قل: 'حبي لك لا يعني أن أوافق على كل شيء. أنا أرفض هذا الطلب، لكن هذا لا يغير مشاعري'. افصل العاطفة عن الطلب.",
            psychologicalPrinciple = "فصل العاطفة عن الطلب (Emotional-Demand Separation).",
            chapterId = 2
        ),
        CaseStudy(
            number = 81,
            question = "إذا كان شخص 'ضحية دائمة' ولا يأخذ مسؤولية أبداً؟",
            behavioralAnalysis = "دور الضحية الدائم هو 'آلية تجنب للمسؤولية' (Responsibility Avoidance Mechanism).",
            practicalResponse = "لا تنقذه دائماً. قل: 'أفهم أن الموقف صعب، ما هي الخطوة التي ستقوم بها؟'. اجعله يتحمل المسؤولية.",
            psychologicalPrinciple = "كسر دور الضحية (Breaking the Victim Role).",
            chapterId = 2
        ),
        CaseStudy(
            number = 96,
            question = "كيف تعرف أن شخصاً يحاول التلاعب بك؟",
            behavioralAnalysis = "علامات التلاعب: شعورك بالذنب، الشك في نفسك، الخوف من ردة فعله، الشعور بأنك 'تمشي على قشر بيض'.",
            practicalResponse = "وثق مشاعرك. إذا شعرت بهذه العلامات باستمرار مع شخص معين، فهو متلاعب. ثق بحدسك.",
            psychologicalPrinciple = "الوعي بعلامات التلاعب (Manipulation Awareness).",
            chapterId = 2
        ),
        CaseStudy(
            number = 101,
            question = "كيف تجعل شخصاً يثق بك في أول لقاء؟",
            behavioralAnalysis = "الثقة تُبنى عبر 'إشارات الأمان' (Safety Signals) التي يرسلها دماغك للآخرين في الثواني الأولى.",
            practicalResponse = "ابتسم ابتسامة حقيقية (تصل للعينين - Duchenne Smile)، حافظ على تواصل بصري مريح (60-70% من الوقت)، واستخدم 'تقنية المرآة' الخفيفة: قلّد نبرة صوته وسرعة كلامه بشكل خفيف جداً.",
            psychologicalPrinciple = "بناء الألفة اللاواعية (Unconscious Rapport Building).",
            chapterId = 3
        ),
        CaseStudy(
            number = 111,
            question = "كيف تجعل الناس يشعرون بالراحة بالقرب منك؟",
            behavioralAnalysis = "الراحة تُرسل عبر 'إشارات الانفتاح' (Openness Signals) في لغة الجسد.",
            practicalResponse = "لا تعقد ذراعيك، اجعل كتفيك مسترخيين، وجّه جسمك نحو الشخص المتحدث، وانحنِ قليلاً للأمام عند اهتمامك بحديثه. هذه الإشارات تقول: 'أنا منفتح، أنا مهتم، أنا آمن'.",
            psychologicalPrinciple = "البث اللاواعي للأمان (Unconscious Safety Broadcasting).",
            chapterId = 3
        ),
        CaseStudy(
            number = 134,
            question = "كيف تحفز شخصاً كسولاً أو مؤجلاً؟",
            behavioralAnalysis = "التسويف غالباً 'خوف من الفشل' أو 'شعور بالضخامة' (Overwhelm).",
            practicalResponse = "لا تقل 'هيا اعمل'. بل قل: 'ما هي أصغر خطوة يمكنك القيام بها الآن؟'. قسم الهدف الكبير لخطوات صغيرة جداً. البدء هو الأصعب، لكنه يولد الزخم.",
            psychologicalPrinciple = "كسر التسويف بالصغر (Breaking Procrastination with Smallness).",
            chapterId = 3
        ),
        CaseStudy(
            number = 151,
            question = "إذا شعرت بغضب شديد مفاجئ، ماذا تفعل في اللحظة؟",
            behavioralAnalysis = "الغضب المفاجئ يُنشّط 'الجهاز العصبي الودي' (Sympathetic Nervous System) ويغلق قشرتك الدماغية الأمامية (مركز التفكير المنطقي).",
            practicalResponse = "لا تتصرف فوراً. استخدم قاعدة '6 ثوانٍ': خذ نفساً عميقاً، عدّ من 1 إلى 6 ببطء. هذا يعيد تفعيل القشرة الأمامية. ثم اسأل نفسك: 'هل هذا يستحق ردة فعلي؟'.",
            psychologicalPrinciple = "التنظيم العصبي الفوري (Immediate Nervous System Regulation).",
            chapterId = 4
        ),
        CaseStudy(
            number = 163,
            question = "إذا كنت 'تؤجل' مهام مهمة وتشعر بالذنب؟",
            behavioralAnalysis = "التسويف غالباً 'خوف من الفشل' أو 'شعور بالضخامة' (Overwhelm)، وليس كسلاً.",
            practicalResponse = "قسم المهمة لـ 'خطوة صغيرة جداً' (5 دقائق فقط). ابدأ بها. الزخم يولد الدافع، لا العكس. 'ابدأ قبل أن تكون جاهزاً'.",
            psychologicalPrinciple = "كسر التسويف بالبدء الصغير (Breaking Procrastination with Small Starts).",
            chapterId = 4
        ),
        CaseStudy(
            number = 181,
            question = "إذا كنت تريد بناء عادة جديدة لكن تفشل باستمرار؟",
            behavioralAnalysis = "الفشل في بناء العادات غالباً بسبب 'البدء الكبير' (Big Start) ومحاولة تغيير الكثير بسرعة.",
            practicalResponse = "ابدأ بـ 'عادة صغيرة جداً' (Tiny Habit): دقيقتان فقط يومياً. اربطها بعادة موجودة: 'بعد أن أشرب القهوة، سأقرأ صفحتين'. الصغر يضمن الاستمرارية.",
            psychologicalPrinciple = "العادات الصغيرة (Tiny Habits).",
            chapterId = 4
        ),
        CaseStudy(
            number = 186,
            question = "إذا كنت تحت 'ضغط شديد' وتشعر بـ 'اختناق'؟",
            behavioralAnalysis = "الضغط الشديد يُنشّط 'استجابة القتال أو الهروب' (Fight-or-Flight) بشكل مزمن.",
            practicalResponse = "مارس 'تنفس المربع' (Box Breathing): شهيق 4 ثوانٍ، حبس 4 ثوانٍ، زفير 4 ثوانٍ، حبس 4 ثوانٍ. كرر 5 مرات. هذا يعيد تنشيط الجهاز العصبي فوراً.",
            psychologicalPrinciple = "التنظيم العصبي الفوري (Immediate Nervous System Regulation).",
            chapterId = 4
        ),
        CaseStudy(
            number = 196,
            question = "كيف تعرف أنك 'تتطور' فعلاً وليس فقط 'تتمنى'؟",
            behavioralAnalysis = "التطور الحقيقي يُقاس بـ 'تغيير السلوك' (Behavior Change)، لا بـ 'تغيير الأفكار' (Thought Change).",
            practicalResponse = "اسأل: 'هل أتصرف بشكل مختلف عن الشهر الماضي؟'، 'هل تعاملت مع موقف صعب بطريقة أفضل؟'. التطور في الأفعال، لا في الأمنيات.",
            psychologicalPrinciple = "التطور بالسلوك (Behavioral Growth).",
            chapterId = 4
        )
    )

    fun getCasesForChapter(chapterId: Int): List<CaseStudy> {
        return sampleCases.filter { it.chapterId == chapterId }
    }
}
