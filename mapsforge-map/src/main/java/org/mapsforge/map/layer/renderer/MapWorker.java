/*
 * Copyright 2010, 2011, 2012 mapsforge.org
 *
 * This program is free software: you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.mapsforge.map.layer.renderer;

import org.mapsforge.map.PausableThread;
import org.mapsforge.map.graphics.Bitmap;
import org.mapsforge.map.layer.cache.TileCache;
import org.mapsforge.map.layer.queue.JobQueue;
import org.mapsforge.map.viewinterfaces.LayerManagerInterface;

public class MapWorker extends PausableThread {
	private final DatabaseRenderer databaseRenderer;
	private final JobQueue<RendererJob> jobQueue;
	private final LayerManagerInterface layerManagerInterface;
	private final TileCache tileCache;

	public MapWorker(TileCache tileCache, JobQueue<RendererJob> jobQueue, DatabaseRenderer databaseRenderer,
			LayerManagerInterface layerManagerInterface) {
		super();

		this.tileCache = tileCache;
		this.jobQueue = jobQueue;
		this.databaseRenderer = databaseRenderer;
		this.layerManagerInterface = layerManagerInterface;
	}

	@Override
	protected void doWork() throws InterruptedException {
		RendererJob rendererJob = this.jobQueue.remove();

		Bitmap bitmap = this.databaseRenderer.executeJob(rendererJob);

		if (!isInterrupted() && bitmap != null) {
			this.tileCache.put(rendererJob, bitmap);
			this.layerManagerInterface.redrawLayers();
		}
	}

	@Override
	protected ThreadPriority getThreadPriority() {
		return ThreadPriority.BELOW_NORMAL;
	}

	@Override
	protected boolean hasWork() {
		return true;
	}
}