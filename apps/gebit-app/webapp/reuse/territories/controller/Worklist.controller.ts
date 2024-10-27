import MessageToast from "sap/m/MessageToast";
import Controller from "sap/ui/core/mvc/Controller";
import AppComponent from "../Component";
import UIComponent from "sap/ui/core/UIComponent";
import ODataModel from "sap/ui/model/odata/v4/ODataModel";
import View from "sap/ui/core/mvc/View";
import Control from "sap/ui/core/Control";
import Dialog from "sap/m/Dialog";
import { ObjectBindingInfo } from "sap/ui/base/ManagedObject";
import JSONModel from "sap/ui/model/json/JSONModel";
import Table from "sap/m/Table";
import ODataListBinding from "sap/ui/model/odata/v4/ODataListBinding";
import Event from "sap/ui/base/Event";
import ColumnListItem from "sap/m/ColumnListItem";
import FileUploader from "sap/ui/unified/FileUploader";
import Label from "sap/m/Label";
import XMLModel from "sap/ui/model/xml/XMLModel";

/**
 * @namespace ui5.gebit.app.reuse.territories.controller
 */           
export default class Worklist extends Controller {

	createDialog:Dialog;
	public onInit() : void {
		const view = this.getView() as View
		if (view) {
			view.addStyleClass((this.getOwnerComponent() as AppComponent).getContentDensityClass());
		}
	}

	public onUpdateFinished(oEvent:any) {
		console.log("onUpdateFinished");
	} 

	public openCreateDialog(oEvent:any) {
		let that = this;
		if(this.createDialog == null) {
			this.loadFragment({name:"ui5.gebit.app.reuse.territories.fragment.UploadKmlContent", addToDependents: true}).then(function(dialog:any){
				that.createDialog = dialog as Dialog;	
				let uiModel = (that.createDialog.getModel("uiModel") as JSONModel);
				uiModel.setProperty("/create/kml", {
					isXmlParsed : false,
					name : "",
					link: "",
					content: {
						
					}
				});

				that.createDialog.bindElement({
					path: "/create/kml/territory",
					model:"uiModel"
				});
				that.createDialog.open();

				
			}.bind(this));
		} else {
			that.createDialog.open();
			(that.createDialog.getModel("uiModel") as JSONModel).setProperty("/create/kml", {});
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
		(this.getView()?.getModel("uiModel") as JSONModel).setProperty("/create", {});
		this.createDialog.close();
	}


	public onPressListItem (oEvent:Event) {
		let oBindingContext = (oEvent.getSource() as ColumnListItem).getBindingContext();
		let router = (this.getOwnerComponent() as UIComponent).getRouter();
		router.navTo("detail",{
			"id" : oBindingContext?.getProperty("ID")
		})
		console.log();
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
		console.log(territory);

		(this.getView()?.getModel("uiModel") as JSONModel).setProperty("/create/kml/territory", territory);
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
}