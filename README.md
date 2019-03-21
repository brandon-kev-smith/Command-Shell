Introduction

A shell is a user program or environment provides for user interaction.  
The shell is a command language interpreter that accepts commands from 
standard input (the keyboard) and executes them. A shell may simply 
accept user commands and pass them off to the operating system to 
execute them, it may additionally provide mechanisms, such as redirecting
the output of one program to the input of another.

Commands

ptime

    The shell tracks the amount of time spent executing child processes. 
    It does not track time for built-in shell commands.  
    Whenever the command 'ptime' is entered into the shell, 
    the cumulative number of seconds spent executing (waiting for) child processes 
    is displayed.
    
history

    The shell keeps a history of the previous commands entered, with no maximum hard-coded history length.
    If the user enters the command 'history', the shell provides a listing of the complete command shell history.
    
^ <number>
    
    If the user enters the command '^ 10', for example, the 10th command in the history buffer will be executed
    (a 1 based index). This command also goes into the history.  If the user then selects that command to be 
    executed from history, the command it refers to should be executed, for as deep as the history execution 
    chain indicates.

list

    On Linux there is an external 'ls' command to display the contents of the current folder.  
    On Windows there isn't an external utility to do this, instead it is a built-in shell command.  
    This shell provides equivalent functionality on Linux and Windows, 
    List will display the content similarly to Linux "ls -v" commmand.

cd

    The 'cd' command allows the user to change the working directory of the shell. 
    The command only allows for changing one directory at a time, e.g., 'cd ..' or 'cd src'.  
    Commands like, 'cd ../../src' are not supported. 
    If the user enters the command "cd" without any parameters, 
    the shell will change the working directory to the user's home folder.

|

    The shell supports piping the output of one command to the input of another, using the vertical bar "|" 
    as the pipe separator.  It only supports piping between two (external) commands.

exit

    The command 'exit' is used to terminate the shell program, when entered, the shell program ends.
