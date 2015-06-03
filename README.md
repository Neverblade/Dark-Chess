# Dark-Chess
A server-client pair of programs for playing Dark Chess, a variant of chess with limited information.

In dark chess, the only squares a player can see are the pieces they own and the squares they can move to - 
excluding pawns, who can see the square above, above-left, and above-right of them.

To play, run the server on someone's computer, then open up instances of the client program and connect to the server
through use of the server computer's IP. Without a centralized server location, IP must be inputted manually and might
pose some difficulties (for instance, connection between different networks might require port forwarding, while connection
on the same network only needs use of the public IP).

The game is held and saved on the server, so clients can be opened and closed without issue. The first two clients to join 
will be assigned their team of black or white randomly. People who join after the teams are filled are given spectator mode,
where all pieces can be seen but but board cannot be interacted with.

To-Do List:

     Create a graphics frame for the server so the user doesn't need to close the server through task manager.
     Create additional functionality for spectator mode as, such as switching between different team visions.
     Clean up some things for the chess client.
