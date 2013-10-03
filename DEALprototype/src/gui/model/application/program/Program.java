package gui.model.application.program;

/**
 * Predstavuje program, ktorý sa má vykonať na spúšťanej aplikácii. Skladá
 * sa z postupnosti príkazov Command.
 */
public class Program {

   /** Postupnosť príkazov, ktoré sa majú vykonať na spúšťanej aplikácii. */
   private final Command[] commands;
   /** Index práve vykonávaného príkazu. */
   private int index = 0;

   /**
    * Konštruktor.
    * @param commands zoznam príkazov, ktoré sa majú vykonať na spúšťanej aplikácii.
    */
   public Program(Command[] commands) {
      this.commands = commands;
   }

   /**
    * @return Zoznam príkazov commands.
    */
   public Command[] getCommands() {
      return commands;
   }

   /**
    * Vykoná program nad zadaným komponentom.
    * @param <T> trieda komponentu.
    * @param component komponent, nad ktorým sa má vykonať program.
    */
   public <T> void execute(T component) {
      execute(component, getCommands().length);
   }

   /**
    * Vykoná program nad zadaným komponentom s použitím krokovača.
    * @param <T> trieda komponentu.
    * @param component komponent, nad ktorým sa má vykonať program.
    * @param step krok, v ktorm je vykonávanie programu.
    */
   public <T> void execute(T component, int step) {
      while (index < getCommands().length) {
         Command command = getCommands()[index++];
         if (!command.execute(component)) {
            String cannotExecuteString = "Cannot execute command " + command;
            if (command instanceof DelegateCommand) {
               Parameter[] parameters = ((DelegateCommand) command).getParameters();
               if (parameters != null)
                  if (parameters.length != 0) {
                     cannotExecuteString = cannotExecuteString + " (";
                     for (int i = 0; i < parameters.length; i++) {
                        cannotExecuteString = cannotExecuteString + parameters[i].toString();
                        if (i != parameters.length - 1) {
                           cannotExecuteString = cannotExecuteString + " ";
                        }
                     }
                     cannotExecuteString = cannotExecuteString + ")";
                  }
            }
            System.out.println(cannotExecuteString);
            index--;
            return;
         }
         step--;
         if (step == 0) {
            return;
         }
      }
   }
}
