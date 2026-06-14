package com.example.geoquiz

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update


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

class GeoQuizViewModel : ViewModel() {
    private val _state = MutableStateFlow(QuizState())
    val state: StateFlow<QuizState> = _state

    fun onAnswer(userAnswer: Boolean) {
        val current = _state.value
        val updatedAnswers = current.userAnswers.toMutableList()
        updatedAnswers[current.currentIndex] = userAnswer
        _state.update {
            it.copy(
                userAnswers = updatedAnswers,
                questionAnswered = true
            )
        }
    }

    fun onNextQuestion() {
        _state.update { state ->
            val nextIndex = state.currentIndex + 1
            if (nextIndex < state.questions.size) {
                state.copy(
                    currentIndex = nextIndex,
                    questionAnswered = false
                )
            } else {
                state.copy(showResultDialog = true)
            }
        }
    }

    fun dismissResultDialog() {
        _state.update { it.copy(showResultDialog = false) }
    }

    val correctCount: Int
        get() {
            val state = _state.value
            return state.questions.indices.count { index ->
                state.userAnswers[index] == state.questions[index].correctAnswer
            }
        }

    val resultText: String
        get() = "Правильных ответов: $correctCount из ${_state.value.questions.size}"
}