package Controller;

import Model.Cell;
import Model.Grid;
import Model.HexModel;
import View.HexView;

import java.awt.*;
import java.awt.event.*;

/**
 * Abomnes Gauthier
 * Bretheau Yann
 * S3C
 */

public class HexController implements ActionListener,MouseListener{

    private HexModel model;
    private HexView view;

    /** ************
     *
     *  Constructeur
     *
     *  ************
     */

    public HexController(HexModel model, HexView view){
        this.model = model;
        this.view = view;

        view.pMenu.bPlayB.addActionListener(this);
        view.pMenu.bPlayR.addActionListener(this);
        view.pMenu.bReset.addActionListener(this);
        view.pMenu.bQuit.addActionListener(this);

        view.pGame.addMouseListener(this);
        view.pGame.bReturn.addActionListener(this);

        view.pVictory.panel.bReturn.addActionListener(this);

    }

    /** ******************
     *
     *  Action des boutons
     *
     *  Les attributs du model enJeu et enCours permettent de connaitre l'état du jeu.
     *  L'attribut enJeu permet de différencier le panel du menu et du jeu et l'attribut enCours permet de savoir si une partie est en cours.
     *  Grâce à l'attribut enCours on peut proposer différente action dans le menu, comme une action de reset si une partie est en cours.
     *
     *  ******************
     */

    @Override
    public void actionPerformed(ActionEvent e) {
        /**
         * Action dans la fenêtre du menu sans que le jeu sois lancé
         */
        if (!model.getInGame() && !model.getCurrentGame()) {
            // Si on clique sur le bouton play le jeu se lance
            if (e.getSource() == view.pMenu.bPlayB) {
                model.setInGame(true);
                model.setCurrentGame(true);
                playRandom();
            }
            else if (e.getSource() == view.pMenu.bPlayR) {
                model.setInGame(true);
                model.setCurrentGame(true);
                model.setIAColor(Color.RED);
            }
            else if (e.getSource() == view.pMenu.bQuit) {
                // Si on clique sur le bouton quitter on quitte le jeu
                view.dispose();
            }
        }

        /**
         * Action dans la fenêtre du menu avec le jeu lancé
         */
        else if (!model.getInGame() && model.getCurrentGame()){
            // Si on clique sur le bouton play on retourne au jeu et on continue la partie en cours
            if (e.getSource() == view.pMenu.bPlayB) {
                model.setInGame(true);
                model.setCurrentGame(true);
            }
            else if (e.getSource() == view.pMenu.bReset){
                // Si on clique sur le bouton reset on relance une nouvelle partie
                model.rebuild();
                model.setInGame(true);
                model.setCurrentGame(true);
            }
            else if (e.getSource() == view.pMenu.bQuit) {
                // Si on clique sur le bouton quitter on quitte le jeu
                view.dispose();
            }
        }

        /**
         * Action dans la fenêtre de jeu
         */
        else if (model.getInGame() && model.getCurrentGame()) {
            // Si on clique sur le bouton retour on retourne au menu et la partie est toujours en cours, c'est à dire qu'on pourra y revenir
            // en appuyant sur play dans le menu
            if (e.getSource() == view.pGame.bReturn) {
                model.setInGame(false);
                model.setCurrentGame(true);
                view.pVictory.setVisible(false);
            }
            // Si on clique sur le bouton retour de la fenêtre de victoire on retourne au menu, ce qui arrête la partie en cours puisqu'on a eu un gagant
            if(e.getSource() == view.pVictory.panel.bReturn) {
                view.pVictory.setVisible(false);
                model.rebuild();
            }
        }
    }

    /** **************************
     *
     *  Action de la souris en jeu
     *
     *  **************************
     */

    @Override
    public void mouseClicked(MouseEvent e) {
        if (model.getInGame())
        {
            // On recupère les coordonnées du clique de la souris
            float x = e.getX();
            float y = e.getY();

            /**
             * Pour toutes les cellules de la grid si les coordonnées de la souris sont comprises dans une cellule de la grid alors on change la couleur
             * de cette cellule avec la couleur du joueur en courant.
             * On appel aussi la méthode victoire du model après chaque clique du joueur. On test la victoire par rapport a la couleur du joueur.
             */
            for (Cell c : model.grid) {
                if (c.contains(x,y) && c.getColor() == Color.WHITE) {
                    if (model.getPlayer() == Color.BLUE){
                        c.setColor(Color.BLUE);
                        model.setPlayer(Color.RED);
                        // On test la victoire pour le joueur bleu, c'est à dire en partant de la cellule bleu en 0,1
                        model.researchVictory(Color.BLUE);
                        this.playIA(Color.RED, Color.BLUE, c.getX(), c.getY());
                    }
                    else{
                        c.setColor(Color.RED);
                        model.setPlayer(Color.BLUE);
                        // On test la victoire pour le joueur rouge, c'est à dire en partant de la cellule rouge en 1,0
                        model.researchVictory(Color.RED);
                        this.playIA(Color.BLUE, Color.RED, c.getX(), c.getY());
                    }
                }
            }

        }
    }

    public void playRandom() {
        int nbLines = model.getGrid().getNbLines();
        int nbColumns = model.getGrid().getNbColumns();
        int newX = (int) (Math.random()*(nbLines - 0));
        int newY = (int) (Math.random()*(nbColumns - 0));
        Cell temporaryCell = model.getGrid().getCell(newX, newY);
        while (temporaryCell.getColor() != Color.WHITE) {
            newX = (int) (Math.random()*(nbLines - 0));
            newY = (int) (Math.random()*(nbColumns - 0));
            temporaryCell = model.getGrid().getCell(newX, newY);
        }
        if (model.getPlayer() == Color.BLUE){
            temporaryCell.setColor(Color.BLUE);
            model.setPlayer(Color.RED);
            // On test la victoire pour le joueur bleu, c'est à dire en partant de la cellule bleu en 0,1
            model.researchVictory(Color.BLUE);
        }
        else{
            temporaryCell.setColor(Color.RED);
            model.setPlayer(Color.BLUE);
            // On test la victoire pour le joueur rouge, c'est à dire en partant de la cellule rouge en 1,0
            model.researchVictory(Color.RED);
        }
    }

    public void playIA (Color player1, Color player2, int x, int y) {
        if (player1 == Color.RED) {
            Cell c = model.getGrid().getCell(x+1,y);
            if (c.getColor() != Color.WHITE) {
                c = model.getGrid().getCell(x-1,y);
                if (c.getColor() != Color.WHITE) {
                    c = model.getGrid().getCell(x,y+1);
                    if (c.getColor() != Color.WHITE) {
                        c = model.getGrid().getCell(x,y-1);
                        if (c.getColor() != Color.WHITE) {
                            this.playRandom();
                        }
                        else {
                            c.setColor(player1);
                            model.setPlayer(player2);
                            model.researchVictory(player1);
                        }
                    }
                    else {
                        c.setColor(player1);
                        model.setPlayer(player2);
                        model.researchVictory(player1);
                    }
                }
                else {
                    c.setColor(player1);
                    model.setPlayer(player2);
                    model.researchVictory(player1);
                }
            }
            else {
                c.setColor(player1);
                model.setPlayer(player2);
                model.researchVictory(player1);
            }

        }
        else {
            Cell c = model.getGrid().getCell(x,y+1);
            if (c.getColor() != Color.WHITE) {
                c = model.getGrid().getCell(x,y-1);
                if (c.getColor() != Color.WHITE) {
                    c = model.getGrid().getCell(x+1,y);
                    if (c.getColor() != Color.WHITE) {
                        c = model.getGrid().getCell(x-1,y);
                        if (c.getColor() != Color.WHITE) {
                            this.playRandom();
                        }
                        else {
                            c.setColor(player1);
                            model.setPlayer(player2);
                            model.researchVictory(player1);
                        }
                    }
                    else {
                        c.setColor(player1);
                        model.setPlayer(player2);
                        model.researchVictory(player1);
                    }
                }
                else {
                    c.setColor(player1);
                    model.setPlayer(player2);
                    model.researchVictory(player1);
                }
            }
            else {
                c.setColor(player1);
                model.setPlayer(player2);
                model.researchVictory(player1);
            }
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }
}
