package com.erzhan.mynote.feature_note.domain.use_case

import com.erzhan.mynote.feature_note.domain.model.InvalidNoteException
import com.erzhan.mynote.feature_note.domain.model.Note
import com.erzhan.mynote.feature_note.repository.FakeNoteRepository
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertThrows
import org.junit.Before
import org.junit.Test

class AddNoteUseCaseTest {

    private lateinit var addNoteUseCase: AddNoteUseCase
    private lateinit var getNotesUseCase: GetNotesUseCase
    private lateinit var fakeNoteRepository: FakeNoteRepository

    @Before
    fun setUp() {
        fakeNoteRepository = FakeNoteRepository()
        getNotesUseCase = GetNotesUseCase(fakeNoteRepository)
        addNoteUseCase = AddNoteUseCase(fakeNoteRepository)

        val notesToInsert = mutableListOf<Note>()
        ('a'..'c').forEachIndexed { index, ch ->
            notesToInsert.add(
                Note(
                    title = ch.toString(),
                    content = ch.toString(),
                    timestamp = index.toLong(),
                    color = index
                )
            )
        }
        notesToInsert.shuffle()
        runBlocking {
            notesToInsert.forEach { fakeNoteRepository.insertNote(it) }
        }
    }

    @Test
    fun `Empty title add to list Exception` () = runBlocking {
        val note = Note(
            title = "",
            content = "This is test content",
            timestamp = 1L,
            color = 1
        )

        val exception = assertThrows(InvalidNoteException::class.java) {
            runBlocking {
                addNoteUseCase(note)
            }
        }

        val expectedMessage = "The title of the note cannot be empty."
        val actualMessage = exception.message

        assertThat(actualMessage).isEqualTo(expectedMessage)

    }

    @Test
    fun `Empty content add to list Exception` () = runBlocking {
        val note = Note(
            title = "This test",
            content = "",
            timestamp = 3L,
            color = 3
        )

        val exception = assertThrows(InvalidNoteException::class.java) {
            runBlocking {
                addNoteUseCase(note)
            }
        }

        val expectedMessage = "The content of the note cannot be empty."
        val actualMessage = exception.message

        assertThat(actualMessage).isEqualTo(expectedMessage)
    }

    @Test
    fun `Add note to list Exception` () = runBlocking {
        val note = Note(
            title = "Test title",
            content = "This is test content",
            timestamp = 2L,
            color = 2
        )
        addNoteUseCase(note)

        val notes = getNotesUseCase().first()

        assertThat(notes.contains(note)).isTrue()
    }

}