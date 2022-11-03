/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package prac1.exceptions;

/**
 * Error que es mostra quan es vol carregar un arxiu d'àudio que ja existeix al
 * llistat del reproductor.
 *
 * @author Txell Llanas
 */
public class DuplicatedItemException extends Exception {

    public DuplicatedItemException(String msg) {
        super(msg);
    }

}
