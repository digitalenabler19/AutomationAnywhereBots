package com.automationanywhere.botcommand.samples.iterator;

import com.automationanywhere.bot.service.GlobalSessionContext;
import com.automationanywhere.botcommand.BotCommand;
import com.automationanywhere.botcommand.data.Value;
import com.automationanywhere.botcommand.exception.BotCommandException;
import com.automationanywhere.commandsdk.i18n.Messages;
import com.automationanywhere.commandsdk.i18n.MessagesFactory;
import java.lang.ClassCastException;
import java.lang.Deprecated;
import java.lang.Object;
import java.lang.String;
import java.lang.Throwable;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class DateDifferenceCommandCommand implements BotCommand {
  private static final Logger logger = LogManager.getLogger(DateDifferenceCommandCommand.class);

  private static final Messages MESSAGES_GENERIC = MessagesFactory.getMessages("com.automationanywhere.commandsdk.generic.messages");

  @Deprecated
  public Optional<Value> execute(Map<String, Value> parameters, Map<String, Object> sessionMap) {
    return execute(null, parameters, sessionMap);
  }

  public Optional<Value> execute(GlobalSessionContext globalSessionContext,
      Map<String, Value> parameters, Map<String, Object> sessionMap) {
    logger.traceEntry(() -> parameters != null ? parameters.entrySet().stream().filter(en -> !Arrays.asList( new String[] {}).contains(en.getKey()) && en.getValue() != null).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)).toString() : null, ()-> sessionMap != null ?sessionMap.toString() : null);
    DateDifferenceCommand command = new DateDifferenceCommand();
    HashMap<String, Object> convertedParameters = new HashMap<String, Object>();
    if(parameters.containsKey("source") && parameters.get("source") != null && parameters.get("source").get() != null) {
      convertedParameters.put("source", parameters.get("source").get());
      if(convertedParameters.get("source") !=null && !(convertedParameters.get("source") instanceof ZonedDateTime)) {
        throw new BotCommandException(MESSAGES_GENERIC.getString("generic.UnexpectedTypeReceived","source", "ZonedDateTime", parameters.get("source").get().getClass().getSimpleName()));
      }
    }
    if(convertedParameters.get("source") == null) {
      throw new BotCommandException(MESSAGES_GENERIC.getString("generic.validation.notEmpty","source"));
    }

    if(parameters.containsKey("source1") && parameters.get("source1") != null && parameters.get("source1").get() != null) {
      convertedParameters.put("source1", parameters.get("source1").get());
      if(convertedParameters.get("source1") !=null && !(convertedParameters.get("source1") instanceof ZonedDateTime)) {
        throw new BotCommandException(MESSAGES_GENERIC.getString("generic.UnexpectedTypeReceived","source1", "ZonedDateTime", parameters.get("source1").get().getClass().getSimpleName()));
      }
    }
    if(convertedParameters.get("source1") == null) {
      throw new BotCommandException(MESSAGES_GENERIC.getString("generic.validation.notEmpty","source1"));
    }

    if(parameters.containsKey("unit") && parameters.get("unit") != null && parameters.get("unit").get() != null) {
      convertedParameters.put("unit", parameters.get("unit").get());
      if(convertedParameters.get("unit") !=null && !(convertedParameters.get("unit") instanceof String)) {
        throw new BotCommandException(MESSAGES_GENERIC.getString("generic.UnexpectedTypeReceived","unit", "String", parameters.get("unit").get().getClass().getSimpleName()));
      }
    }
    if(convertedParameters.get("unit") == null) {
      throw new BotCommandException(MESSAGES_GENERIC.getString("generic.validation.notEmpty","unit"));
    }
    if(convertedParameters.get("unit") != null) {
      switch((String)convertedParameters.get("unit")) {
        case "MILLIS" : {

        } break;
        case "SECONDS" : {

        } break;
        case "MINUTES" : {

        } break;
        case "HOURS" : {

        } break;
        case "DAYS" : {

        } break;
        case "WEEKS" : {

        } break;
        case "MONTHS" : {

        } break;
        case "YEARS" : {

        } break;
        default : throw new BotCommandException(MESSAGES_GENERIC.getString("generic.InvalidOption","unit"));
      }
    }

    try {
      Optional<Value> result =  Optional.ofNullable(command.action((ZonedDateTime)convertedParameters.get("source"),(ZonedDateTime)convertedParameters.get("source1"),(String)convertedParameters.get("unit")));
      return logger.traceExit(result);
    }
    catch (ClassCastException e) {
      throw new BotCommandException(MESSAGES_GENERIC.getString("generic.IllegalParameters","action"));
    }
    catch (BotCommandException e) {
      logger.fatal(e.getMessage(),e);
      throw e;
    }
    catch (Throwable e) {
      logger.fatal(e.getMessage(),e);
      throw new BotCommandException(MESSAGES_GENERIC.getString("generic.NotBotCommandException",e.getMessage()),e);
    }
  }

  public Map<String, Value> executeAndReturnMany(GlobalSessionContext globalSessionContext,
      Map<String, Value> parameters, Map<String, Object> sessionMap) {
    return null;
  }
}
