package ru.netology.nmedia.activity

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast

import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import ru.netology.nmedia.R
import ru.netology.nmedia.adapter.OnInteractorListener
import ru.netology.nmedia.adapter.PostAdapter
import ru.netology.nmedia.databinding.ActivityMainBinding
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.viewmodel.PostViewModel

class MainActivity : AppCompatActivity() {

    private val viewModel: PostViewModel by viewModels()
    private var oldContent: String = ""   //  point 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.editingGroup.visibility = View.GONE  // group

        val adapter = PostAdapter(
            object: OnInteractorListener{
                override fun onLike(post: Post) {
                    viewModel.like(post.id)
                }

                override fun onRemove(post: Post) {
                    viewModel.removeById(post.id)
                }

                override fun onEdit(post: Post) {
                    viewModel.edit(post)
                }

                override fun onShare(post: Post) {
                    viewModel.share(post.id)
                }

                override fun onEye(post: Post) {
                    viewModel.look(post.id)
                }
            }
        )

        binding.list.adapter = adapter

        viewModel.data.observe(this) { posts ->
            val isNew = posts.size != adapter.itemCount
            adapter.submitList(posts){
                if (isNew) {
                    binding.list.smoothScrollToPosition(0)
                }
            }
        }

        viewModel.edited.observe(this) { post ->
            if (post.id != 0L) {
                oldContent = post.content           //   point 2
                with(binding.content) {
                    requestFocus()
                    // focusAndShowKeyboard()
                    setText(post.content)
                }
                binding.editingGroup.visibility = View.VISIBLE  // group
            } else{
                binding.editingGroup.visibility = View.GONE     // group
                binding.content.setText("")
                binding.content.clearFocus()
            }
        }

        with(binding) {
            save.setOnClickListener {
                if (content.text.isNullOrBlank()) {
                    Toast.makeText(
                        this@MainActivity,
                        R.string.error_empty_content,
                        Toast.LENGTH_LONG
                    ).show()
                    return@setOnClickListener
                }
                viewModel.changeContent(content.text.toString())
                viewModel.save()
                content.setText("")
                content.clearFocus()
                AndroidUtils.hideKeyboard(it)
                editingGroup.visibility = View.GONE     //  group
            }

            buttomCancel.setOnClickListener {
//                content.setText("")
                content.setText(oldContent)             //  point 3
                content.clearFocus()
                AndroidUtils.hideKeyboard(it)
                editingGroup.visibility = View.GONE     //  group
                viewModel.edit(Post(id=0))
            }
        }
    }

    object AndroidUtils{
        fun hideKeyboard(view: View){
            val imm = view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }
}