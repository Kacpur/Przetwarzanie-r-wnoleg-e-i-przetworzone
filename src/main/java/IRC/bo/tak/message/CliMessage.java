package IRC.bo.tak.message;

import java.util.ArrayList;
import java.util.List;

public class CliMessage
{

    private String message;
    private String prefix;
    private String command;
    private List<String> parameters;

    public CliMessage(String message)
    {
        this.message = message;
        prefix = "";
        command = "";
        parameters = new ArrayList<String>();

        String[] message_split_s = this.message.split("\\s+");

        /* Detect if there's prefix or not */
        if (this.message.startsWith(":"))
        {
            prefix = message_split_s[0];
            command = message_split_s[1];

            String parameters = "";

            for (int i = 2; i < message_split_s.length; i++)
            {
                parameters += ((i == 2) ? "" : " ") + message_split_s[i];
            }

            parseParameters(parameters);
        }
        else
        {
            prefix = "";
            command = message_split_s[0];

            String parameters = "";

            for (int i = 1; i < message_split_s.length; i++)
            {
                parameters += ((i == 1) ? "" : " ") + message_split_s[i];
            }

            parseParameters(parameters);
        }
    }

    private void parseParameters(String parameters)
    {
        /* Detect what kind of parameters we are receiving */
        String[] params_split_s = parameters.split("\\s+");
        String[] params_split_c = parameters.split("\\s*,\\s*");

        /* Parameters separated by , for some commands */
        if ((command.equalsIgnoreCase("JOIN") || command.equalsIgnoreCase("PART")) && params_split_c.length > 1)
        {
            for (String s : params_split_c)
            {
                this.parameters.add(s.trim());
            }
        }
        /* Parameters separated by SPACE */
        else
        {
            List<List<String>> params_combined = new ArrayList<List<String>>();
            int param_separator = 0;

            for (String s : params_split_s)
            {
                if (s.startsWith(":"))
                {
                    params_combined.add(new ArrayList<String>());
                    param_separator++;
                }

                if (params_combined.size() == 0 && !s.startsWith(":"))
                {
                    this.parameters.add(s);
                }
                else
                {
                    params_combined.get(param_separator - 1).add(s);
                }
            }

            for (List<String> l : params_combined)
            {
                String parameter = "";

                for (String p : l)
                {
                    parameter += p + " ";
                }

                parameter = parameter.replace(":", "");

                if (!parameter.trim().isEmpty())
                {
                    this.parameters.add(parameter.trim());
                }
            }
        }
    }

    public String getMessage()
    {
        return message;
    }

    public String getPrefix()
    {
        return prefix;
    }

    public String getCommand()
    {
        return command;
    }

    public List<String> getParameters()
    {
        return parameters;
    }

    public String getParameter(int i)
    {
        if (i < 0 || i >= parameters.size())
        {
            return "";
        }

        return parameters.get(i);
    }

}
