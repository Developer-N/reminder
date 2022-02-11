package ir.namoo.reminder.ui

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.namoo.reminder.db.Word
import ir.namoo.reminder.db.WordDB
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WordViewModel @Inject constructor(private val db: WordDB) : ViewModel() {

    private val _words = MutableLiveData<List<Word>>()
    val words: MutableLiveData<List<Word>> get() = _words

    private val _word = MutableLiveData<Word>()
    val word: MutableLiveData<Word> get() = _word

    init {
        viewModelScope.launch {
            _words.value = db.WordDao().getAllWords()
        }
    }

    fun insertWord(word: Word) {
        viewModelScope.launch {
            db.WordDao().insert(word)
            _words.value = db.WordDao().getAllWords()
        }
    }

    fun updateWord(word: Word) {
        viewModelScope.launch {
            db.WordDao().update(word)
            _words.value = db.WordDao().getAllWords()
        }
    }

    fun getWordByID(id: Int) {
        viewModelScope.launch {
            _word.value = db.WordDao().getWordByID(id)
        }
    }

    fun deleteWord(word: Word) {
        viewModelScope.launch {
            db.WordDao().delete(word)
            _words.value = db.WordDao().getAllWords()
        }
    }

}