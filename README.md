# Pathfinder
This application helps people plan their day based on all the work they have to finish. 

When a user has to finish a task they can drop a pin at the location they have to finish the task and enter details like the deadline for the task, what the task is and the amount of time the task will take. All the tasks are stored in a database so that they persist over multiple instances of the app. The location can be selected by clicking on that location, or searching for it. Geocoder APIs are used to get a human readable name for the places. When a location is selected, a dialog box comes up that asks for the aforementioned information and then it is saved. 

When a user has free time, they can enter the amount of time they have into the app. Then a weighted genetic algorithm, that works based on the time taken to go from place to place, the time taken at the place and the deadline for the task, to chart a path that would finish as many tasks as possible in a day in the amount of time available. This algorithm is in a separate respoitory called PathfinderAlgorithm.

This will tell the user what path to take on that day to finish as much work as possible.
