/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package prac1.exceptions;

/**
    * Error que es mostra quan es vol carregar un arxiu d'àudio sense duració 
    * (00:00) des del diàleg de selecció d'arxius del sistema operatiu.
    * 
    * @author Txell Llanas
    */
        public class NoDurationException extends Exception {

        public NoDurationException(String msg) {
            super(msg);
        }
    }