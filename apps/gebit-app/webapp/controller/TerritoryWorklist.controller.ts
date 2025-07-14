import MessageToast from "sap/m/MessageToast";
import Controller from "sap/ui/core/mvc/Controller";
import AppComponent from "../Component";
import UIComponent from "sap/ui/core/UIComponent";
import View from "sap/ui/core/mvc/View";
import Dialog from "sap/m/Dialog";
import JSONModel from "sap/ui/model/json/JSONModel";
import Table from "sap/m/Table";
import ODataListBinding from "sap/ui/model/odata/v4/ODataListBinding";
import Event from "sap/ui/base/Event";
import ColumnListItem from "sap/m/ColumnListItem";
import { Router$RouteMatchedEvent } from "sap/ui/core/routing/Router";
import {Formatter} from "./FormatterUtils"
import ODataModel from "sap/ui/model/odata/v4/ODataModel";
import MessageBox from "sap/m/MessageBox";
import SearchField from "sap/m/SearchField";
import Filter from "sap/ui/model/Filter";
import FilterOperatorUtil from "sap/ui/mdc/condition/FilterOperatorUtil";
import FilterType from "sap/ui/model/FilterType";
import FilterOperator from "sap/ui/model/FilterOperator";
import Sorter from "sap/ui/model/Sorter";
import TerritoryFilterContainer from "./utils/FilterContainer";
import ViewSettingsItem from "sap/m/ViewSettingsItem";
/**
 * @namespace ui5.gebit.app.controller
 */           
export default class TerritoryWorklist extends Controller {

	createDialog:Dialog;
	isModelInitialized:Boolean;
	selectedTerritoryBindingContext:any;
	filterContainer: TerritoryFilterContainer = new TerritoryFilterContainer(this);
	sortDialog: Dialog;
	public onInit() : void {
		const view = this.getView() as View
		if (view) {
			view.addStyleClass((this.getOwnerComponent() as AppComponent).getContentDensityClass());
		}

		let router = (this.getOwnerComponent() as UIComponent).getRouter();
		router.attachRouteMatched(this.attachRouteMatched, this);
		
	}


	public attachRouteMatched(oEvent:Router$RouteMatchedEvent) {
		let routeName = oEvent.getParameter("name");
		if(routeName == "territories" && this.isModelInitialized) {
			this.getView()?.getModel()?.refresh();
		}
		this.isModelInitialized = true;
	}
	

	public openCreateDialog(oEvent:any) {
		let that = this;
		if(this.createDialog == null) {
			this.loadFragment({name:"ui5.gebit.app.fragment.UploadKmlContent", addToDependents: true}).then(function(dialog:any){
				that.createDialog = dialog as Dialog;
				that.createDialog.bindElement({
					path: "/create/kml/territory",
					model:"uiModel"
				});
				that.createDialog.open();

				
			}.bind(this));
		} else {
			// (this.getView()?.getModel("uiModel") as JSONModel).setProperty("/create/kml/isXmlParsed", false);
			// (this.getView()?.getModel("uiModel") as JSONModel).setProperty("/create/kml/territory", {});
			that.createDialog.open();

		}
	}

	public createDialogClose() {
		this.closeCreateDialog();
	}

	public createTerritory() {
		var oList = (this.byId("territoriesTable") as Table)
		let oBinding = oList.getBinding("items") as ODataListBinding;
		let oContext = oBinding.create(this.getView()?.getModel("uiModel")?.getProperty("/create/kml/territory"));
		
			
			if(oContext != undefined) {
				oContext.created().then(function (obj:any) {
					this.closeCreateDialog();
				}.bind(this), function (oError:any) {
					// handle rejection of entity creation; if oError.canceled === true then the transient entity has been deleted
						if (!oError.canceled) {
							throw oError; // unexpected error
						}
				});
		}
	}

	private closeCreateDialog() {
		(this.getView()?.getModel("uiModel") as JSONModel).setProperty("/create/kml/isXmlParsed", false);
		(this.getView()?.getModel("uiModel") as JSONModel).setProperty("/create/kml/territory", {});
		this.createDialog.close();
	}


	public onPressListItem (oEvent:Event) {
		let oBindingContext = (oEvent.getSource() as ColumnListItem).getBindingContext();
		let router = (this.getOwnerComponent() as UIComponent).getRouter();
		router.navTo("territoryDetail",{
			"id" : oBindingContext?.getProperty("ID")
		})
	}


	public handleValueChange(oEvent:Event) {

		let file = oEvent.getParameter("files")[0];
		var reader = new FileReader();
		reader.onload = function (evt) {
			this.parseXmlToJSON(evt.target.result);
		}.bind(this);
		reader.readAsText(file, "UTF-8");
	}

	private parseXmlToJSON(xml:string) {
		(this.getView()?.getModel("uiModel") as JSONModel).setProperty("/create/kml/isXmlParsed", true);
		// xml = xml.replace(new RegExp('\>[ ]+\<', 'g'), '><');
		xml = xml.replace(new RegExp('\>[\s]+\<', 'g'), '><');

		let kmlParser = new KmlParser(xml);

		let territory = kmlParser.getTerritoryCreateObj();

		(this.getView()?.getModel("uiModel") as JSONModel).setProperty("/create/kml/territory", territory);
	}

	public onSelectionChange(oEvent:Event) {
		this.byId("deleteTerritoryButton").setEnabled(oEvent.getParameter("listItem").getBindingContext() != null);	
	}

	public async onDelete(oEvent:Event) {
		debugger;
		let oList = (this.byId("territoriesTable") as Table);
		let oSelectedContext = oList.getSelectedContexts()[0];
		let model = this.getView()?.getModel() as ODataModel;
		let i18nText = this.getView()?.getModel("i18n")?.getProperty("confirmDeleteTerritoryText");
		MessageBox.confirm(i18nText, {
			title: "Confirm",
			onClose: async function (action) {
				if (action === MessageBox.Action.OK) {

						await model.delete(oSelectedContext.getPath());

				} else {
					this.getView()?.getModel().refresh();
				}
			}.bind(this), 
			styleClass: "",
			actions: [ MessageBox.Action.OK, MessageBox.Action.CANCEL ],
			emphasizedAction: MessageBox.Action.OK,
			initialFocus:  MessageBox.Action.CANCEL
		});
		
	}

	public onSearch(oEvent: Event) {
		let table = this.getView().byId("territoriesTable") as Table;
		let oBinding = table.getBinding("items") as ODataListBinding;
		var sQuery = oEvent.getParameter("query");
		this.filterContainer.addSearchFilter(sQuery);
		let filterAggregate = this.filterContainer.getResultingFilters();
		oBinding.filter(filterAggregate);

	}

	public async onFilterSelect (oEvent:Event) {
		
		let sKey = oEvent.getParameter("key");
		let oBinding = this.getView()?.byId("territoriesTable")?.getBinding("items") as ODataListBinding;
		
		// oBinding.filter(new Filter(aFilters, false), FilterType.Application);
		this.filterContainer.addIconTabFilter(sKey);
		let filterAggregate = this.filterContainer.getResultingFilters();
		
		
		if(sKey === "All") 
			(this.getView()?.byId("territorySearchField") as SearchField).setValue();

		// if(sKey === "RarelyWorked") {
		// 	oBinding.filter(filterAggregate).sort([new Sorter("lastTimeWorked", false)]);
		// } else {
		// 	oBinding.filter(filterAggregate);
		// }
			oBinding.filter(filterAggregate);
		
	}


	public handleSortButtonPressed(oEvent: Event) {
		let that = this;
		if(this.sortDialog == null) {
			this.loadFragment({name:"ui5.gebit.app.fragment.TerritorySortDialog", addToDependents: true}).then(function(dialog:any){
				that.createDialog = dialog as Dialog;
				that.createDialog.open();
			}.bind(this));
		} else {
			// (this.getView()?.getModel("uiModel") as JSONModel).setProperty("/create/kml/isXmlParsed", false);
			// (this.getView()?.getModel("uiModel") as JSONModel).setProperty("/create/kml/territory", {});
			that.createDialog.open();

		}
		
	}
	public createSortDialogClose() {
		this.sortDialog.close();
	}

	public handleSortDialogConfirm(oEvent: Event) {
		let viewSettingsItem = oEvent.getParameter("sortItem") as ViewSettingsItem;
		let isDescending = oEvent.getParameter("sortDescending") as boolean;
		let oBinding = this.getView()?.byId("territoriesTable")?.getBinding("items") as ODataListBinding;
		viewSettingsItem.getKey();
		oBinding.sort([new Sorter(viewSettingsItem.getKey(), isDescending)]);
	}
	}

class KmlParser {
	xmlDoc: XMLDocument;
	constructor(xml:string) {
		var parser = new DOMParser();
		this.xmlDoc = parser.parseFromString(xml, "application/xml");
	}

	public getTerritoryCreateObj() {
		let result = {
			name : this.xmlDoc.getElementsByTagName("Document")[0].getElementsByTagName("name")[0].textContent,
			isReady: false,
		};
		
		
		let placemarks = Array.from(this.xmlDoc.getElementsByTagName("Placemark"));

		let parts = placemarks.map((placemark:any) => {
			let coordinates = placemark.getElementsByTagName("coordinates")[0].textContent;

			let coordinatesArray = coordinates.trim().split(/\s+/);

			// Group the coordinates in arrays of three (lat, long, alt)
			let groupedCoordinates = [];
			for (let point of coordinatesArray) {
				let pointParts = point.split(",");
				if(pointParts.length == 3 ) {
					point = pointParts[0] + "," + pointParts[1];
				}
				groupedCoordinates.push(`[${point.split(',')}]`);
			}

			// let poligon = {
			// 	type: "Polygon",
			// 	coordinates: [
			// 		[
			// 			[x,y],
			// 			[a,b]
			// 		]
			// 	]
			// }	

			// Join all the groups into the desired format and output
			let output = `[${groupedCoordinates.join(",")}]`;

			let part = {
				name: placemark.getElementsByTagName("name")[0].textContent,
				coordinates: output
			};

			return part;
			
		} );

		result.toParts = parts;

		return result;

	}

	public formatStartEndDate(value:string) {
		return Formatter.formatDateColumn(value);
	}
}