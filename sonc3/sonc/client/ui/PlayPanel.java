package sonc.client.ui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.google.gwt.animation.client.AnimationScheduler;
import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.user.cellview.client.CellList;
import com.google.gwt.user.cellview.client.HasKeyboardSelectionPolicy.KeyboardSelectionPolicy;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.FlexTable.FlexCellFormatter;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SingleSelectionModel;

/*
 * TODO:
 * 		Load a Cell List with all players
 * 		DecPanel with player info and buttons "Add to battle", "Remove from battle"
 * 		"Battle" button calls Manager.battle and starts animation																																											
 */

public class PlayPanel extends FlexTable {

	private AnimationScheduler scheduler = AnimationScheduler.get();
	private Canvas movie;
	private Context2d movieObject;
	private List<String> players = Arrays.asList("Player1", "Player2");
	private List<String> battlers = Arrays.asList("Player1");
	private CellList playerList;
	
	public PlayPanel() {
		this.setCellSpacing(6);
		this.setWidget(5, 5, zp.getColourZp());
		FlexCellFormatter cellFormatter = this.getFlexCellFormatter();
		
	    TextCell textCell = new TextCell();
	    CellList<String> available = new CellList<String>(textCell);
	    CellList<String> inBattle = new CellList<String>(textCell);
	    
	    // Add a selection model to handle user selection.
	    final SingleSelectionModel<String> selectionModel = new SingleSelectionModel<String>();
	    available.setSelectionModel(selectionModel);
	    selectionModel.addSelectionChangeHandler(new SelectionChangeEvent.Handler() {
	      public void onSelectionChange(SelectionChangeEvent event) {
	        String selected = selectionModel.getSelectedObject();
	      }
	    });	

	    available.setRowCount(players.size(), true);
	    available.setRowData(0, players);
	    cellFormatter.setRowSpan(0, 0, 2);
	    this.setWidget(0, 0, available);
	    
	    inBattle.setRowCount(players.size(), true);
	    inBattle.setRowData(0, battlers);
	    cellFormatter.setRowSpan(0, 2, 2);
	    this.setWidget(0, 2, inBattle);
	    
	    VerticalPanel buttons = new VerticalPanel();
	    Button button = new Button("Add to battle");
	    Button button2 = new Button("Remove");
	    buttons.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
	    buttons.add(button);
	    buttons.add(button2);
	    cellFormatter.setRowSpan(0, 1, 2);
	    this.setWidget(0, 1, buttons);
	    
	}
	
}
