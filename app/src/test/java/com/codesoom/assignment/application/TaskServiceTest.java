package com.codesoom.assignment.application;

import com.codesoom.assignment.TaskNotFoundException;
import com.codesoom.assignment.models.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("TaskService 클래스")
class TaskServiceTest {

    private TaskService taskService;

    private static final String TASK_TITLE = "test";
    private static final String UPDATE_POSTFIX = "!!!";
    private static final Long INVALID_ID = 0L;

    @BeforeEach
    void setUp() {
        taskService = new TaskService();
    }

    @Nested
    @DisplayName("getTask 메소드는")
    class Describe_getTask {
        @Nested
        @DisplayName("등록된 Task가 있다면")
        class Context_has_task {
            final int givenTaskCnt = 5;

            @BeforeEach
            void prepare() {
                for (int i = 0; i < givenTaskCnt; i++) {
                    taskService.createTask(getTask());
                }
            }

            @Test
            @DisplayName("Task 전체 리스트를 리턴한다.")
            void it_return_tasks() {
                assertThat(taskService.getTasks()).hasSize(givenTaskCnt);
            }
        }

        @Nested
        @DisplayName("등록된 Task가 없다면")
        class Context_has_not_task {

            @BeforeEach
            void prepare() {
                List<Task> tasks = taskService.getTasks();
                tasks.forEach(task -> taskService.deleteTask(task.getId()));
            }

            @Test
            @DisplayName("빈 리스트를 리턴한다.")
            void it_return_tasks() {
                assertThat(taskService.getTasks()).isEmpty();
            }
        }

        @Nested
        @DisplayName("등록된 Task의 id 값이 주어진다면")
        class Context_with_id {
            Task givenTask;
            Long givenId() {
                return givenTask.getId();
            }

            @BeforeEach
            void prepare() {
                givenTask = taskService.createTask(getTask());
            }

            @Test
            @DisplayName("등록된 task 정보를 리턴한다.")
            void it_return_task() {
                Task foundTask = taskService.getTask(givenId());

                assertThat(foundTask).isNotNull();
            }
        }

        @Nested
        @DisplayName("등록되지 않은 Task의 id 값이 주어진다면")
        class Context_with_invalid_id {
            @Test
            @DisplayName("Task를 찾을 수 없다는 내용의 예외를 던진다.")
            void it_return_taskNotFoundException() {
                assertThatThrownBy(() -> taskService.getTask(INVALID_ID)).isInstanceOf(TaskNotFoundException.class);
            }
        }
    }

    @Nested
    @DisplayName("createTask 메소드는")
    class Describe_createTask {
        @Nested
        @DisplayName("등록할 Task가 주어진다면")
        class Context_with_task {
            Task givenTask;

            @BeforeEach
            void prepare() {
                givenTask = getTask();
            }

            @Test
            @DisplayName("Task를 생성하고, 리턴한다.")
            void it_create_task_return_task() {
                int sizeBeforeCreation = getTasksSize();

                Task createdTask = taskService.createTask(givenTask);

                int sizeAfterCreation = getTasksSize();

                assertThat(createdTask.getTitle()).isEqualTo(givenTask.getTitle());
                assertThat(sizeAfterCreation - sizeBeforeCreation).isEqualTo(1);
            }
        }

        @Nested
        @DisplayName("null 주어진다면")
        class Context_without_task {
            Task givenNullTask = null;

            @Test
            @DisplayName("NullPointerException을 던진다.")
            void it_return_exception() {
                assertThatThrownBy(() -> taskService.createTask(givenNullTask)).isInstanceOf(NullPointerException.class);
            }
        }
    }

    @Nested
    @DisplayName("updateTask 메소드는")
    class Describe_updateTask {
        @Nested
        @DisplayName("등록된 Task의 id 와 수정할 Task 가 주어진다면")
        class Context_with_id_and_task {
            Task givenTask;
            Task givenSource;
            Long givenId() {
                return givenTask.getId();
            }

            @BeforeEach
            void prepare() {
                givenTask = taskService.createTask(getTask());
                givenSource = getTaskWithPostfix();
            }

            @Test
            @DisplayName("해당 id의 Task를 수정하고, 리턴한다.")
            void it_update_task_return_task() {
                Task updatedTask = taskService.updateTask(givenId(), givenSource);

                assertThat(updatedTask.getTitle()).isEqualTo(givenSource.getTitle());
            }
        }

        @Nested
        @DisplayName("등록된 Task의 id과 null 주어진다면")
        class Context_with_id {
            Task givenTask;
            Task givenNullTask = null;
            Long givenId() {
                return givenTask.getId();
            }

            @BeforeEach
            void prepare() {
                givenTask = taskService.createTask(getTask());
            }

            @Test
            @DisplayName("NullPointerException을 던진다.")
            void it_update_task_return_task() {
                assertThatThrownBy(() -> taskService.updateTask(givenId(), givenNullTask)).isInstanceOf(NullPointerException.class);
            }
        }

        @Nested
        @DisplayName("등록된 Task가 null로 주어진다면")
        class Context_with_task {
            Long givenNullId = null;

            @Test
            @DisplayName("Task를 찾을 수 없다는 내용의 예외를 던진다.")
            void it_return_taskNotFoundException() {
                assertThatThrownBy(() -> taskService.updateTask(givenNullId, getTaskWithPostfix())).isInstanceOf(TaskNotFoundException.class);
            }
        }

        @Nested
        @DisplayName("등록되지 않은 Task의 id 와 Task가 있다면 ")
        class Context_with_invalid_id_and_task {
            @Test
            @DisplayName("Task를 찾을 수 없다는 내용의 예외를 던진다.")
            void it_return_taskNotFoundException() {
                assertThatThrownBy(() -> taskService.updateTask(INVALID_ID, getTask())).isInstanceOf(TaskNotFoundException.class);
            }
        }
    }

    @Nested
    @DisplayName("deleteTask 메소드")
    class Describe_deleteTask {
        @Nested
        @DisplayName("등록된 Task의 id가 주어진다면")
        class Context_with_id {
            Task givenTask;
            Long givenId() {
                return givenTask.getId();
            }

            @BeforeEach
            void prepare() {
                givenTask = taskService.createTask(getTask());
            }

            @Test
            @DisplayName("등록된 Task를 삭제하고, 빈값이 리턴한다.")
            void it_delete_task_return() {
                int sizeBeforeDeletion = getTasksSize();

                taskService.deleteTask(givenId());

                int sizeAfterDeletion = getTasksSize();

                assertThat(sizeBeforeDeletion - sizeAfterDeletion).isEqualTo(1);
            }
        }

        @Nested
        @DisplayName("등록되지 않은 Task의 id가 주어진다면")
        class Context_with_invalid_id {
            @Test
            @DisplayName("Task를 찾을 수 없다는 내용의 예외를 던진다.")
            void it_return_taskNotFoundException() {
                assertThatThrownBy(() -> taskService.deleteTask(INVALID_ID)).isInstanceOf(TaskNotFoundException.class);
            }
        }
    }

    private int getTasksSize() {
        return taskService.getTasks().size();
    }

    private Task getTask() {
        Task task = new Task();
        task.setTitle(TASK_TITLE);

        return task;
    }

    private Task getTaskWithPostfix() {
        Task task = new Task();
        task.setTitle(TASK_TITLE + UPDATE_POSTFIX);

        return task;
    }
}
