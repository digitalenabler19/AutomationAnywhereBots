package com.automationanywhere.botcommand.psd.trigger.validatepdf;

import com.automationanywhere.botcommand.data.Value;
import com.automationanywhere.botcommand.data.impl.RecordValue;
import com.automationanywhere.botcommand.data.impl.StringValue;
import com.automationanywhere.botcommand.data.model.Schema;
import com.automationanywhere.botcommand.data.model.record.Record;
import com.automationanywhere.commandsdk.annotations.*;
import com.automationanywhere.commandsdk.annotations.rules.NotEmpty;
import com.automationanywhere.commandsdk.model.AttributeType;
import org.springframework.jms.listener.MessageListenerContainer;

import java.io.IOException;
import java.nio.file.*;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;


import static com.automationanywhere.commandsdk.model.DataType.RECORD;

@BotCommand(commandType = BotCommand.CommandType.Trigger)
@CommandPkg(label = "PDF Trigger", description = "PDF Trigger", icon = "jms.svg", name = "pdfTrigger",
        return_type = RECORD, return_name = "TriggerData", return_description = "Available keys: triggerType")
public class ValidatePdfFile {

    // Map storing multiple MessageListenerContainer
    private static final Map<String, MessageListenerContainer> taskMap = new ConcurrentHashMap<>();

    @TriggerId
    private String triggerUid;
    @TriggerConsumer
    private Consumer consumer;

    private RecordValue getRecordValue() {
        List<Schema> schemas = new LinkedList<>();
        List<Value> values = new LinkedList<>();
        schemas.add(new Schema("triggerType"));
        values.add(new StringValue("ValidatePdfFile"));

        RecordValue recordValue = new RecordValue();
        recordValue.set(new Record(schemas,values));
        return recordValue;
    }
    /*
     * Starts the trigger.
     *
     * Use this method to setup the trigger, such as, setup the MessageListenerContainer and start it.
     */
    @StartListen
    public void startTrigger(@Idx(index = "1", type = AttributeType.TEXT)
                             @Pkg(label = "Provide the broker URL")
                             @NotEmpty
                             String folderPath) {

        if (taskMap.get(triggerUid) == null) {
            synchronized (this) {
                if (taskMap.get(triggerUid) == null) {
                    try {
                        WatchService watchService = FileSystems.getDefault().newWatchService();
                        Path folder = Paths.get(folderPath);
                        folder.register(watchService, StandardWatchEventKinds.ENTRY_CREATE);

                        System.out.println("Monitoring folder for new files: " + folderPath);

                        while (true) {
                            WatchKey key = watchService.take();
                            for (WatchEvent<?> event : key.pollEvents()) {
                                WatchEvent.Kind<?> kind = event.kind();

                                if (kind == StandardWatchEventKinds.ENTRY_CREATE) {
                                    Path filepath = folder.resolve((Path) event.context());

                                    if(filepath.toString().toLowerCase().endsWith(".pdf")){
                                        System.out.println("New file added: " + filepath);
                                        consumer.accept(getRecordValue());
                                        return;
                                    }
                                }



// Perform your desired action here

                            }
                            key.reset();
                        }
                    } catch (IOException | InterruptedException e) {
                        e.printStackTrace();
                    }

                }
            }
        }

    }

    /*
     * Cancel all the tasks and clear the map.
     */
    @StopAllTriggers
    public void stopAllTriggers() {
        taskMap.forEach((k, v) -> {
            v.stop();
            taskMap.remove(k);
        });
    }

    /*
     * Cancel the tasks and remove from the map
     *
     * @param triggerUid
     */
    @StopListen
    public void stopListen(String triggerUid) {
        taskMap.get(triggerUid).stop();
        taskMap.remove(triggerUid);
    }

    public String getTriggerUid() {
        return triggerUid;
    }

    public void setTriggerUid(String triggerUid) {
        this.triggerUid = triggerUid;
    }

    public Consumer getConsumer() {
        return consumer;
    }

    public void setConsumer(Consumer consumer) {
        this.consumer = consumer;
    }

}
