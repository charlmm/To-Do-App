package com.charles.mvvmto_doapp.ui.tasks

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.charles.mvvmto_doapp.data.PreferencesManager
import com.charles.mvvmto_doapp.data.SortOrder
import com.charles.mvvmto_doapp.data.Task
import com.charles.mvvmto_doapp.data.TaskDao
import com.charles.mvvmto_doapp.ui.ADD_TASK_RESULT_OK
import com.charles.mvvmto_doapp.ui.EDIT_TASK_RESULT_OK
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class TasksViewModel @ViewModelInject constructor(
 private val taskDao: TaskDao,
 private val preferencesManager: PreferencesManager,
 @Assisted private val state: SavedStateHandle
): ViewModel(){

 val searchQuery = state.getLiveData("searchQuery", "")

 val preferencesFlow = preferencesManager.preferenceFlow

 private val tasksEventChannel = Channel<TaskEvent>()
 val taskEvent = tasksEventChannel.receiveAsFlow()

 private val taskFlow = combine(
  searchQuery.asFlow(),
  preferencesFlow
 ){ query, filterPreferences->
  Pair(query, filterPreferences)
 }.flatMapLatest { (query, filterPreferences) ->
  taskDao.getTasks(query, filterPreferences.sortOrder, filterPreferences.hideCompleted)
 }

 fun onSortOrderSelected(sortOrder: SortOrder) = viewModelScope.launch {
  preferencesManager.updateSortOrder(sortOrder)
 }

 fun onHideCompletedClicked(hideCompleted: Boolean) = viewModelScope.launch {
  preferencesManager.updateHideCompleted(hideCompleted)
 }

 fun onTaskSelected(task: Task) = viewModelScope.launch {
  tasksEventChannel.send(TaskEvent.NavigateToEditTaskScreen(task))
 }

 fun onTaskCheckedChanged(task: Task, isChecked: Boolean) = viewModelScope.launch {
   taskDao.update(task.copy(completed = isChecked))
 }

 fun onTaskSwiped(task: Task) = viewModelScope.launch {
  taskDao.delete(task)
  tasksEventChannel.send(TaskEvent.ShowUndoDeleteTaskMessage(task))
 }

 fun onUndoTaskDelete(task: Task) = viewModelScope.launch {
  taskDao.insertTask(task)
 }

 fun onAddNewTaskClicked() = viewModelScope.launch {
   tasksEventChannel.send(TaskEvent.NavigateToAddTaskScreen)
 }

 fun onAddEditResult(result: Int) {
   when (result) {
     ADD_TASK_RESULT_OK -> showTaskSavedConfirmationMessage("Task Added")
     EDIT_TASK_RESULT_OK -> showTaskSavedConfirmationMessage("Task Edited")
   }
 }

 private fun showTaskSavedConfirmationMessage(text: String) = viewModelScope.launch {

 }

 sealed class TaskEvent {
  object NavigateToAddTaskScreen: TaskEvent()
  data class NavigateToEditTaskScreen(val task: Task): TaskEvent()
  data class ShowUndoDeleteTaskMessage(val task: Task): TaskEvent()
  data class ShoeTaskSavedConfirmationMessage(val message: String): TaskEvent()
 }
 val tasks = taskFlow.asLiveData()
}