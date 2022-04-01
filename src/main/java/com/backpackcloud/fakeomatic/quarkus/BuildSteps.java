/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2020 Marcelo Guimarães <ataxexe@backpackcloud.com>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.backpackcloud.fakeomatic.quarkus;

import io.quarkus.deployment.annotations.BuildProducer;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.builditem.nativeimage.ReflectiveClassBuildItem;

//TODO create extensions for this
public class BuildSteps {

  @BuildStep
  void registerClasses(BuildProducer<ReflectiveClassBuildItem> reflectionClasses) {
    reflectionClasses.produce(new ReflectiveClassBuildItem(true, false, com.backpackcloud.sampler.Sample.class));
    reflectionClasses.produce(new ReflectiveClassBuildItem(true, false, com.backpackcloud.sampler.Sampler.class));
    reflectionClasses.produce(new ReflectiveClassBuildItem(true, false, com.backpackcloud.sampler.SampleConfiguration.class));

    reflectionClasses.produce(new ReflectiveClassBuildItem(true, false, com.backpackcloud.sampler.impl.sample.CachedSample.class));
    reflectionClasses.produce(new ReflectiveClassBuildItem(true, false, com.backpackcloud.sampler.impl.sample.CharSample.class));
    reflectionClasses.produce(new ReflectiveClassBuildItem(true, false, com.backpackcloud.sampler.impl.sample.DateSample.class));
    reflectionClasses.produce(new ReflectiveClassBuildItem(true, false, com.backpackcloud.sampler.impl.sample.ExpressionSample.class));
    reflectionClasses.produce(new ReflectiveClassBuildItem(true, false, com.backpackcloud.sampler.impl.sample.JoiningSample.class));
    reflectionClasses.produce(new ReflectiveClassBuildItem(true, false, com.backpackcloud.sampler.impl.sample.JsonValueSample.class));
    reflectionClasses.produce(new ReflectiveClassBuildItem(true, false, com.backpackcloud.sampler.impl.sample.ListSample.class));
    reflectionClasses.produce(new ReflectiveClassBuildItem(true, false, com.backpackcloud.sampler.impl.sample.RangeSample.class));
    reflectionClasses.produce(new ReflectiveClassBuildItem(true, false, com.backpackcloud.sampler.impl.sample.SourceSample.class));
    reflectionClasses.produce(new ReflectiveClassBuildItem(true, false, com.backpackcloud.sampler.impl.sample.UuidSample.class));
    reflectionClasses.produce(new ReflectiveClassBuildItem(true, false, com.backpackcloud.sampler.impl.sample.WeightedSample.class));
    reflectionClasses.produce(new ReflectiveClassBuildItem(true, false, com.backpackcloud.sampler.impl.sample.TemplateSample.class));
    reflectionClasses.produce(new ReflectiveClassBuildItem(true, false, com.backpackcloud.sampler.impl.sample.HttpSample.class));
    reflectionClasses.produce(new ReflectiveClassBuildItem(true, false, com.backpackcloud.sampler.impl.SamplerImpl.class));

    reflectionClasses.produce(new ReflectiveClassBuildItem(true, false, com.backpackcloud.Configuration.class));
    reflectionClasses.produce(new ReflectiveClassBuildItem(false, false, com.backpackcloud.impl.jackson.ConfigurationDeserializer.class));
  }

}
