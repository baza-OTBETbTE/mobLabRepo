package com.example.geoquiz


data class Question(
    val text: String,
    val correctAnswer: Boolean
)

data class QuizState(
    val questions: List<Question> = listOf(
        Question("Canberra is the capital of Australia.", true),
        Question("The Pacific Ocean is larger than the Atlantic Ocean.", true),
        Question("The Suez Canal connects the Red Sea and the Indian Ocean.", false),
        Question("The source of the Nile River is in Egypt.", false),
        Question("The Amazon River is the longest river in the Americas.", true),
        Question("Lake Baikal is the world's oldest and deepest freshwater lake.", true)
    ),
    val currentIndex: Int = 0,
    val userAnswers: List<Boolean?> = List(6) { null },
    val questionAnswered: Boolean = false,
    val showResultDialog: Boolean = false
)