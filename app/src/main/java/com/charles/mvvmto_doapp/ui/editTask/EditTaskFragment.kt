package com.charles.mvvmto_doapp.ui.editTask

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.charles.mvvmto_doapp.R
import com.charles.mvvmto_doapp.databinding.FragmentEditTaskBinding
import com.charles.mvvmto_doapp.util.exhaustive
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class EditTaskFragment: Fragment(R.layout.fragment_edit_task) {
    private val viewModel: EditTaskViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentEditTaskBinding.bind(view)

        binding.apply {
            editTaskName.setText(viewModel.taskName)
            cbImportant.isChecked = viewModel.taskImportance
            cbImportant.jumpDrawablesToCurrentState()
            tvDateCreated.isVisible = viewModel.task != null
            tvDateCreated.text = "Created: ${viewModel.task?.createdDateFormatted}"

            editTaskName.addTextChangedListener {
                viewModel.taskName = it.toString()
            }
            cbImportant.setOnCheckedChangeListener { _, isChecked ->
                viewModel.taskImportance = isChecked
            }

            fabSaveTask.setOnClickListener {
                viewModel.onSaveClick()
            }
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.addEditTaskEvent.collect { event ->
                when (event) {
                    is EditTaskViewModel.AddEditTaskEvent.NavigateBackWithResults -> {
                        binding.editTaskName.clearFocus()
                        setFragmentResult(
                            "add_edit_result",
                            bundleOf("add_edit_result" to event.result)
                        )
                        findNavController().popBackStack()
                    }
                    is EditTaskViewModel.AddEditTaskEvent.ShowInvalidInputMessage -> {
                        Snackbar.make(requireView(), event.msg, Snackbar.LENGTH_LONG).show()
                    }
                }.exhaustive
            }
        }
    }
}