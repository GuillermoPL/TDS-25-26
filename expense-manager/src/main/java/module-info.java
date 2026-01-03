module umu.tds.expensemanager { 
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.base;
    requires javafx.graphics;
    requires com.fasterxml.jackson.core;
    requires com.fasterxml.jackson.annotation;
    requires com.fasterxml.jackson.databind;
    requires org.apache.logging.log4j;
    requires com.fasterxml.jackson.datatype.jsr310; 

    opens umu.tds.vista to javafx.fxml;

    opens umu.tds.modelo to javafx.base, com.fasterxml.jackson.databind;
    
    opens umu.tds.adapters.repository.impl to com.fasterxml.jackson.databind;

    exports umu.tds;
    exports umu.tds.vista; 
    exports umu.tds.modelo;
    exports umu.tds.controlador; 
    exports umu.tds.adapters.repository;
}