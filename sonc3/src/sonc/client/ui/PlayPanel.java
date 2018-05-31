package sonc.client.ui;
import java.util.ArrayList;
import java.util.List;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.cellview.client.CellList;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DecoratorPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SingleSelectionModel;

import sonc.client.SoncServiceAsync;
import sonc.client.UserData;
import sonc.client.events.EventManager;
import sonc.client.rpc.InvokeButton;
import sonc.client.rpc.InvokeClickHandler;
import sonc.client.ui.animation.Animation;
import sonc.shared.Movie;
import sonc.shared.SoncException;

/**
 * Panel where battles can be set up and movies played
 */
public class PlayPanel extends FlexTable {

	private final InvokeClickHandler<?> handlerMovie = EventManager.getClickHandler(Movie.class);
	private final InvokeClickHandler<?> handlerList  = EventManager.getClickHandler(ArrayList.class);
	
	private final DecoratorPanel decPanel            = new DecoratorPanel();
	private final DecoratorPanel scorePanel          = new DecoratorPanel();
	private final FlexTable battleMenu               = new FlexTable();
	
	private final BattleButton battleButton          = new BattleButton("Start the battle");
	private final UpdateButton updateButton          = new UpdateButton("Update players list");
	private final Button addShip                     = new Button("Add this ship");
	private final Button removeShip                  = new Button("Remove this ship");
	
	private final String EMPTY_PLAYERS_LIST          = "No players have submitted valid ships";
	private final String NO_SHIPS_SELECTED           = "You haven't selected any ships yet";
	private final String CELL_WIDTH                  = "50px";
	
	private Canvas canvas;
	private Context2d context;
	private int canvasWidth  = 1000;
	private int canvasHeight = 1000;
	
	private CellList<String> playerScoreList;
	private CellList<String> playersAvailable;
	private CellList<String> playersSelected;
	private List<String> playerList = new ArrayList<String>();
	private List<String> battleList = new ArrayList<String>();
	private String selectedName;
	private String toRemove;
	
	@SuppressWarnings("unused")
	private UserData userData;
	private SoncServiceAsync soncSvc;
	
	/**
	 * Click handler specific to PlayPanel
	 */
	private ClickHandler clickHandler = new ClickHandler() {
		@Override
		public void onClick(ClickEvent event) {
			Button source = (Button) event.getSource();
			if (source == addShip) {
				battleList.add(selectedName);
				playersSelected.setRowData(battleList);
			} else if (source == removeShip) {
				battleList.remove(toRemove);
				playersSelected.setRowData(battleList);
			}
		}
	};
	
	/**
	 * Button to get a movie from the server
	 */
	private class BattleButton extends InvokeButton<Movie> {
		public BattleButton(String html) {
			super(html, handlerMovie);
		}

		@Override
		public void onSucess(Movie result) {
			Animation animation = new Animation(canvas, playerScoreList, result);
			animation.play();
		}

		@Override
		public void onCall(AsyncCallback<Movie> callback) {
			try {
				soncSvc.testBattle(callback);
			} catch (SoncException e) {
				Window.alert(e.toString());
			}
		}
	}
	
	/**
	 * Button to retrieve player list from the server
	 */
	private class UpdateButton extends InvokeButton<List<String>> {
		public UpdateButton(String html) {
			super(html, handlerList);
		}

		@Override
		public void onSucess(List<String> result) {
			playerList = new ArrayList<String>(result);
			playersAvailable.setRowCount(playerList.size(), true);
			playersAvailable.setRowData(0, playerList);
		}

		@Override
		public void onCall(AsyncCallback<List<String>> callback) {
			soncSvc.getPlayersNamesWithShips(callback);
		}
	}

	/**
	 * this isn't working
	 */
	private void loadPlayerList() {
		updateButton.click();
	}

	/**
	 * Create a new Canvas and initialize it
	 */
	private void loadCanvas() {
		canvas = Canvas.createIfSupported();
		canvas.setWidth(1000 + "px");
		canvas.setCoordinateSpaceWidth(1000);
		canvas.setHeight(1000 + "px");
		canvas.setCoordinateSpaceHeight(1000);

		context = canvas.getContext2d();
		context.setFillStyle("#0099CC");
		context.fillRect(0, 0, canvasHeight, canvasWidth);
	}

	/**
	 * Create and initialize all the necessary cell lists and add their event
	 * handlers
	 */
	private void loadCellLists() {
		TextCell textCell = new TextCell();

		playersAvailable = new CellList<String>(textCell);
		playersAvailable.setTitle("Avaliable Ships");
		playersAvailable.setEmptyListWidget(new HTML(EMPTY_PLAYERS_LIST));
		playersAvailable.setWidth(CELL_WIDTH);
		
		final SingleSelectionModel<String> selectionModel = new SingleSelectionModel<String>();
		playersAvailable.setSelectionModel(selectionModel);
		selectionModel.addSelectionChangeHandler(new SelectionChangeEvent.Handler() {
			public void onSelectionChange(SelectionChangeEvent event) {
				String selected = selectionModel.getSelectedObject();
				if (selected != null) {
					selectedName = selected;
				}
			}
		});
		
		playersSelected = new CellList<String>(textCell);
		playersSelected.setTitle("Selected Ships");
		playersSelected.setEmptyListWidget(new HTML(NO_SHIPS_SELECTED));
		playersSelected.setRowCount(playerList.size(), true);
		playersSelected.setWidth(CELL_WIDTH);
		
		final SingleSelectionModel<String> removeShip = new SingleSelectionModel<String>();
		playersSelected.setSelectionModel(removeShip);
		removeShip.addSelectionChangeHandler(new SelectionChangeEvent.Handler() {
			public void onSelectionChange(SelectionChangeEvent event) {
				String selected = removeShip.getSelectedObject();
				if (selected != null) {
					toRemove = selected;
				}
			}
		});
		
		playerScoreList = new CellList<String>(textCell);
	}

	/**
	 * Add buttons and cell lists to a decorator panel
	 */
	public void loadBattleMenu() {
		battleMenu.setCellPadding(6);
		battleMenu.setWidget(0,0, playersAvailable);
		battleMenu.setWidget(1,0, addShip);
		battleMenu.setWidget(0,1, playersSelected);
		battleMenu.setWidget(1,1, removeShip);
		decPanel.add(battleMenu);
		scorePanel.add(playerScoreList);
	}
	
	public PlayPanel(UserData userData, SoncServiceAsync soncSvc) {
		this.userData = userData;
		this.soncSvc = soncSvc;
		
		loadCanvas();
		loadCellLists();
		loadPlayerList();		
		loadBattleMenu();
		
		addShip.addClickHandler(clickHandler);
		removeShip.addClickHandler(clickHandler);

		FlexCellFormatter cellFormatter = this.getFlexCellFormatter();

		// black magic for everything to align properly
		cellFormatter.setHorizontalAlignment(0, 1, HasHorizontalAlignment.ALIGN_CENTER);
		cellFormatter.setVerticalAlignment(1, 1, HasVerticalAlignment.ALIGN_TOP);
		cellFormatter.setVerticalAlignment(2, 0, HasVerticalAlignment.ALIGN_TOP);
		cellFormatter.setRowSpan(1,0,2);
		cellFormatter.setHeight(1,1,"100px");
		
		this.setWidget(0, 0, battleButton);
		this.setWidget(0, 1, updateButton);
		this.setWidget(1, 0, canvas);
		this.setWidget(1, 1, decPanel);
		this.setWidget(2, 0, scorePanel);
	}
	
}
