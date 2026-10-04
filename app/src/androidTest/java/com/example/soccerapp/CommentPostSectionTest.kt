package com.example.soccerapp

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import com.example.soccerapp.ui.CommentPostSection
import junit.framework.TestCase.assertEquals
import org.junit.Rule
import org.junit.Test

class CommentPostSectionTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun commentPostSection_displaysInputValues() {

        var inputText = ""

        composeTestRule.setContent {
            CommentPostSection(
                author = "TABATA",
                text = "Second",
                onAuthorChange = {},
                onTextChange = {
                    inputText = it
                },
                isPosting = false,
                postErrorMessage = null,
                onPostComment = { _, _ -> }
            )
        }


        composeTestRule
            .onNodeWithTag(testTag = "comment_input")
            .performTextInput("Second")


        assertEquals("Second", inputText)


        composeTestRule
            .onNodeWithText("TABATA")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Second")
            .assertIsDisplayed()
    }


    @Test
    fun commentPostSection_whilePosting_inputIsDisabled() {

        composeTestRule.setContent {
            CommentPostSection(
                author = "TABATA",
                text = "Second",
                onAuthorChange = {},
                onTextChange = {},
                isPosting = true,
                postErrorMessage = null,
                onPostComment = { _, _ -> }
            )
        }


        composeTestRule
            .onNodeWithTag("author_input")
            .assertIsNotEnabled()


        composeTestRule
            .onNodeWithTag("comment_input")
            .assertIsNotEnabled()

        composeTestRule
            .onNodeWithTag("post_button")
            .assertIsNotEnabled()
    }



    @Test
    fun commentPostSection_emptyText_postButtonIsDisabled() {

        composeTestRule.setContent {
            CommentPostSection(
                author = "TABATA",
                text = "",//空欄の場合、テスト。
                onAuthorChange = {},
                onTextChange = {},
                isPosting = false,
                postErrorMessage = null,
                onPostComment = { _, _ -> }
            )
        }

        composeTestRule
            .onNodeWithTag("post_button")
            .assertIsNotEnabled()
    }


    @Test
    fun commentPostSection_clickPost_passesInputValues() {

        var postedAuthor = ""
        var postedText = ""

        composeTestRule.setContent {
            CommentPostSection(
                author = "TABATA",
                text = "Second",
                onAuthorChange = {},
                onTextChange = {},
                isPosting = false,
                postErrorMessage = null,
                onPostComment = { author, text ->
                    postedAuthor = author
                    postedText = text
                }
            )
        }

        composeTestRule
            .onNodeWithTag("post_button")
            .performClick()//ノードをクリック

        assertEquals("TABATA", postedAuthor)
        assertEquals("Second", postedText)
    }




    @Test
    fun commentPostSection_displaysPostError() {

        composeTestRule.setContent {
            CommentPostSection(
                author = "TABATA",
                text = "Second",
                onAuthorChange = {},
                onTextChange = {},
                isPosting = false,
                postErrorMessage = "Post failed",
                onPostComment = { _, _ -> }
            )
        }

        composeTestRule
            .onNodeWithText("Post failed")
            .assertIsDisplayed()
    }


}