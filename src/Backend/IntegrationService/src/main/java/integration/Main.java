package integration;

import integration.scheduled.UpdateProductProcessTask;
import integration.scheduled.CreateOrderProcessTask;
import kieker.monitoring.core.controller.MonitoringController;
import org.springframework.boot.SpringApplication;

public class Main {
	public static void main(String[] args) {
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				MonitoringController.getInstance().terminateMonitoring();
			}
		});
		//Adding the scheduled task classes to the spring application to run in background threads.
		SpringApplication.run(new Object[] { CreateOrderProcessTask.class, UpdateProductProcessTask.class }, new String[0]);
	}
}
