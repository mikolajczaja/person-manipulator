simple service for comparison and classification of differences between string fields of a Person object

exposes CRUD endpoints for Person entity and read endpoints for Task entity (e.g. <host>:<port>/api/v1/tasks for listing all tasks - <host>:<port>/api/v1/tasks/{id} for reading a single task with passed id - for more info on all endpoints check openapi.yaml)

excerpts from specification:
 - A person consists of several fields, such as name, surname, birthdate and
company.
 - Each time a person is created or updated, a task determining classifications of
differences is created.
 - The task should find classifications of differences between respective fields from
the current and previous person. This is stored in a structured form as a task
result.

 - When the person is upserted, the customer receives the unique ID of the task. 
 - The customer can check the task's status and results using the received ID.



running:
